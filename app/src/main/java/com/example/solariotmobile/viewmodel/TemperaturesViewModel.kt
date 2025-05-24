package com.example.solariotmobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.repository.TemperaturesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class TemperaturesViewModel @Inject constructor(private val temperaturesRepository: TemperaturesRepository): ViewModel() {
    private val _loading =  MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _loading
    private val _failure = MutableLiveData(false)
    val isFailure: LiveData<Boolean> get() = _failure
    private val _message = MutableLiveData("")
    val getMessage: LiveData<String> get() = _message
    private val _temperaturesFiltered = MutableLiveData<List<TemperatureDto>>()
    val getTemperaturesFiltered: LiveData<List<TemperatureDto>> get() = _temperaturesFiltered
    private val _temperatures = MutableLiveData<List<TemperatureDto>>()

    fun fetchData() {
        _loading.value = true
        _failure.value = false
        _message.value = ""
        _temperatures.value = mutableListOf()
        _temperaturesFiltered.value = mutableListOf()

        viewModelScope.launch {
            try {
                val response = temperaturesRepository.getTemperatures()

                _loading.value = false

                if (response.isSuccessful) {
                    _temperatures.value =
                        if (response.body() == null) emptyList() else response.body()
                    _temperaturesFiltered.value = _temperatures.value
                } else {
                    _failure.value = true
                    _message.value =
                        if (response.errorBody() == null) "Une erreur est survenue lors de la récupération des températures" else response.errorBody()
                            .toString()
                }
            } catch(exception: Exception) {
                _loading.value = false
                _failure.value = true
                _message.value = exception.message
            }
        }
    }

    fun filterTemperatures(selectedReadingDeviceNames: List<ReadingDeviceName>, selectedDateRange: Pair<Long?, Long?>) {
        val startDate = if (selectedDateRange.first != null) LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedDateRange.first!!), ZoneOffset.UTC).toLocalDate().minusDays(1) else null
        val endDate = if (selectedDateRange.second != null) LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedDateRange.second!!), ZoneOffset.UTC).toLocalDate().plusDays(1) else null
        val datesAreNull = startDate == null || endDate == null

        _temperaturesFiltered.value = _temperatures.value?.filter { temperature ->
            val collectionDate = temperature.collectionDate.toLocalDate()
            (selectedReadingDeviceNames.isEmpty() || selectedReadingDeviceNames.contains(temperature.readingDeviceName))
                    && (datesAreNull ||
                    (collectionDate.isAfter(startDate!!)
                            && collectionDate.isBefore(endDate!!)))
        }
    }

    fun filterCollectionDateOfTemperatures(selectedDateRange: Pair<Long?, Long?>) {
        val startDate = if (selectedDateRange.first != null) LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedDateRange.first!!), ZoneOffset.UTC).toLocalDate().minusDays(1) else null
        val endDate = if (selectedDateRange.second != null) LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedDateRange.second!!), ZoneOffset.UTC).toLocalDate().plusDays(1) else null
        val datesAreNull = startDate == null || endDate == null

        _temperaturesFiltered.value = _temperatures.value?.filter { temperature ->
            val collectionDate = temperature.collectionDate.toLocalDate()
            datesAreNull || (collectionDate.isAfter(startDate!!) && collectionDate.isBefore(endDate!!))
        }
    }

    fun filterReadingDevices(selectedReadingDeviceNames: List<ReadingDeviceName>) {
        _temperaturesFiltered.value = _temperatures.value?.filter { temperature ->
            selectedReadingDeviceNames.contains(temperature.readingDeviceName)
        }
    }

    fun resetFilters() {
        _temperaturesFiltered.value = _temperatures.value
    }
}