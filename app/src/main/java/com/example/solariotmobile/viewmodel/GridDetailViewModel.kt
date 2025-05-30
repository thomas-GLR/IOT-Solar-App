package com.example.solariotmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.repository.TemperaturesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GridDetailViewModel @Inject constructor(private val temperaturesRepository: TemperaturesRepository) :
    ViewModel() {

    // Detail screen
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _selectedReadingDeviceName = MutableStateFlow(ReadingDeviceName.TOP)

    private val _loadingDetail = MutableStateFlow(true)
    val isLoadingDetail: StateFlow<Boolean> get() = _loadingDetail.asStateFlow()
    private val _failureDetail = MutableStateFlow(false)
    val isFailureDetail: StateFlow<Boolean> get() = _failureDetail.asStateFlow()
    private val _messageDetail = MutableStateFlow("")
    val getMessageDetail: StateFlow<String> get() = _messageDetail.asStateFlow()
    private val _temperaturesDetail = MutableStateFlow(emptyList<TemperatureDto>())
    val temperaturesDetail: StateFlow<List<TemperatureDto>> get() = _temperaturesDetail.asStateFlow()

    fun fetchDetailTemperatures(date: LocalDate, readingDeviceName: ReadingDeviceName) {
        updateTemperatureDetailSearchParam(date, readingDeviceName)
        _loadingDetail.value = true
        _failureDetail.value = false
        _messageDetail.value = ""
        _temperaturesDetail.value = mutableListOf()

        viewModelScope.launch {
            try {
                // AggregationType.MONTHS groupe les températures par jours
                val response = temperaturesRepository.getDetailTemperatures(
                    // Je dois enlever 2 heures à cause de nestJs qui ne sait pas gérer les dates correctement
                    _selectedDate.value.atStartOfDay().minusHours(2),
                    _selectedDate.value.plusDays(1).atStartOfDay().minusHours(2),
                    _selectedReadingDeviceName.value
                )

                _loadingDetail.value = false

                println(response.body())

                if (response.isSuccessful) {
                    _temperaturesDetail.value =
                        if (response.body() == null) emptyList() else response.body()!!
                } else {
                    _failureDetail.value = true
                    _messageDetail.value =
                        if (response.errorBody() == null) "Une erreur est survenue lors de la récupération des températures" else response.errorBody()
                            .toString()
                }
            } catch (exception: Exception) {
                _loadingDetail.value = false
                _failureDetail.value = true
                _messageDetail.value =
                    if (exception.message.isNullOrEmpty()) "Une exception inconnue est survenue" else exception.message!!
            }
        }
    }

    private fun updateTemperatureDetailSearchParam(selectedDate: LocalDate, selectedReadingDevice: ReadingDeviceName) {
        _selectedDate.value = selectedDate
        _selectedReadingDeviceName.value = selectedReadingDevice
    }
}