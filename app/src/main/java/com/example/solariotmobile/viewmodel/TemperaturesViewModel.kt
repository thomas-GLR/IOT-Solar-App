package com.example.solariotmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.data.AggregationType
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.repository.TemperaturesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class TemperaturesViewModel @Inject constructor(private val temperaturesRepository: TemperaturesRepository) :
    ViewModel() {
    // Grid Screen
    private val _loading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _loading.asStateFlow()
    private val _failure = MutableStateFlow(false)
    val isFailure: StateFlow<Boolean> get() = _failure.asStateFlow()
    private val _message = MutableStateFlow("")
    val getMessage: StateFlow<String> get() = _message.asStateFlow()
    private val _temperaturesFiltered = MutableStateFlow(emptyList<TemperatureDto>())
    val getTemperaturesFiltered: StateFlow<List<TemperatureDto>> get() = _temperaturesFiltered.asStateFlow()
    private val _temperatures = MutableStateFlow(emptyList<TemperatureDto>())
    private val _resultNumber = MutableStateFlow(0)
    val resultNumber: StateFlow<Int> get() = _resultNumber.asStateFlow()

    private var _selectedReadingDeviceNames: List<ReadingDeviceName> =
        listOf(ReadingDeviceName.TOP, ReadingDeviceName.MIDDLE, ReadingDeviceName.BOTTOM)
    private var _selectedDateRange: Pair<Long?, Long?> = Pair(null, null)

    fun fetchData() {
        _loading.value = true
        _failure.value = false
        _message.value = ""
        _temperatures.value = mutableListOf()
        _temperaturesFiltered.value = mutableListOf()

        viewModelScope.launch {
            try {
                // AggregationType.MONTHS groupe les températures par jours
                val response = temperaturesRepository.getTemperatures(AggregationType.MONTHS)

                _loading.value = false

                if (response.isSuccessful) {
                    _temperatures.value =
                        if (response.body() == null) emptyList() else response.body()!!

                    filterTemperatures()
                    _resultNumber.value = _temperaturesFiltered.value.size
                } else {
                    _failure.value = true
                    _message.value =
                        if (response.errorBody() == null) "Une erreur est survenue lors de la récupération des températures" else response.errorBody()
                            .toString()
                }
            } catch (exception: Exception) {
                _loading.value = false
                _failure.value = true
                _message.value =
                    if (exception.message.isNullOrEmpty()) "Une exception inconnue est survenue" else exception.message!!
            }
        }
    }

    fun updateAndFilterTemperatures(
        selectedReadingDeviceNames: List<ReadingDeviceName>,
        selectedDateRange: Pair<Long?, Long?>
    ) {
        _selectedReadingDeviceNames = selectedReadingDeviceNames
        _selectedDateRange = selectedDateRange
        filterTemperatures()
    }

    private fun filterTemperatures() {
        val startDate =
            if (_selectedDateRange.first != null)
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(_selectedDateRange.first!!),
                    ZoneOffset.UTC
                ).toLocalDate().minusDays(1)
            else null

        val endDate =
            if (_selectedDateRange.second != null) LocalDateTime.ofInstant(
                Instant.ofEpochMilli(_selectedDateRange.second!!), ZoneOffset.UTC
            ).toLocalDate().plusDays(1)
            else null

        val datesAreNull = startDate == null || endDate == null

        _temperaturesFiltered.value = _temperatures.value.filter { temperature ->
            val collectionDate = temperature.collectionDate.toLocalDate()

            (_selectedReadingDeviceNames.isEmpty() || _selectedReadingDeviceNames
                .contains(temperature.readingDeviceName)) && (datesAreNull
                    || (collectionDate.isAfter(startDate!!) && collectionDate.isBefore(endDate!!)))
        }
        _resultNumber.value = _temperaturesFiltered.value.size
    }

    fun filterCollectionDateOfTemperatures(selectedDateRange: Pair<Long?, Long?>) {
        val startDate = if (selectedDateRange.first != null) LocalDateTime.ofInstant(
            Instant.ofEpochMilli(selectedDateRange.first!!), ZoneOffset.UTC
        ).toLocalDate().minusDays(1) else null
        val endDate = if (selectedDateRange.second != null) LocalDateTime.ofInstant(
            Instant.ofEpochMilli(selectedDateRange.second!!), ZoneOffset.UTC
        ).toLocalDate().plusDays(1) else null
        val datesAreNull = startDate == null || endDate == null

        _temperaturesFiltered.value = _temperatures.value.filter { temperature ->
            val collectionDate = temperature.collectionDate.toLocalDate()
            datesAreNull || (collectionDate.isAfter(startDate!!) && collectionDate.isBefore(endDate!!))
        }
        _resultNumber.value = _temperaturesFiltered.value.size
    }

    fun filterReadingDevices(selectedReadingDeviceNames: List<ReadingDeviceName>) {
        _temperaturesFiltered.value = _temperatures.value.filter { temperature ->
            selectedReadingDeviceNames.contains(temperature.readingDeviceName)
        }
        _resultNumber.value = _temperaturesFiltered.value.size
    }

    fun resetFilters() {
        _temperaturesFiltered.value = _temperatures.value
    }
}