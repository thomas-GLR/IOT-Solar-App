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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

enum class MonthFilter(val value: String) {
    ALL("Tous les mois"),
    JANUARY("Janvier"),
    FEBRUARY("Février"),
    MARCH("Mars"),
    APRIL("Avril"),
    MAY("Mai"),
    JUNE("Juin"),
    JULY("Juillet"),
    AUGUST("Aout"),
    SEPTEMBER("Septembre"),
    OCTOBER("Octobre"),
    NOVEMBER("Novembre"),
    DECEMBER("Décembre")
}

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

    private var _selectedReadingDeviceNames = MutableStateFlow(ReadingDeviceName.entries.toList())
    val getSelectedReadingDeviceNames: StateFlow<List<ReadingDeviceName>> get() = _selectedReadingDeviceNames.asStateFlow()
    private var _selectedDateRange: MutableStateFlow<Pair<Long?, Long?>> = MutableStateFlow(Pair(null, null))
    val getSelectedDateRange get() = _selectedDateRange.asStateFlow()

    fun fetchData() {
        _loading.value = true
        _failure.value = false
        _message.value = ""
        _temperatures.value = mutableListOf()
        _temperaturesFiltered.value = mutableListOf()

        viewModelScope.launch {
            try {
                resetFilters()

                // AggregationType.MONTHS groupe les températures par jours
                val response = temperaturesRepository.getTemperatures(AggregationType.MONTHS)

                _loading.value = false

                if (response.isSuccessful) {
                    val temperaturesBody =
                        if (response.body() == null) emptyList() else response.body()!!

                    _temperatures.value = temperaturesBody.sortedWith(
                        compareBy<TemperatureDto> { it.collectionDate }
                            .reversed()
                            .thenBy { it.readingDeviceName }
                        )

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

    fun toggleDateRange(dateRange: Pair<Long?, Long?>) {
        _selectedDateRange.value = dateRange
        filterTemperatures()
    }

    fun toggleReadingDevice(device: ReadingDeviceName) {
        _selectedReadingDeviceNames.update { current ->
            if (device in current) current - device else current + device
        }
        filterTemperatures()
    }

    private fun filterTemperatures() {
        val startDate =
            if (_selectedDateRange.value.first != null)
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(_selectedDateRange.value.first!!),
                    ZoneOffset.UTC
                ).toLocalDate().minusDays(1)
            else null

        val endDate =
            if (_selectedDateRange.value.second != null) LocalDateTime.ofInstant(
                Instant.ofEpochMilli(_selectedDateRange.value.second!!), ZoneOffset.UTC
            ).toLocalDate().plusDays(1)
            else null

        val datesAreNull = startDate == null || endDate == null

        _temperaturesFiltered.value = _temperatures.value.filter { temperature ->
            val collectionDate = temperature.collectionDate.toLocalDate()

            (_selectedReadingDeviceNames.value.isEmpty() || _selectedReadingDeviceNames.value
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
        _selectedDateRange.value = null to null
        _selectedReadingDeviceNames.value = ReadingDeviceName.entries.toMutableList()
        _temperaturesFiltered.value = _temperatures.value
    }
}