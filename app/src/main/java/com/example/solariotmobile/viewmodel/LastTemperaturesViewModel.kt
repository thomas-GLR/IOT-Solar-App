package com.example.solariotmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.repository.TemperaturesRepository
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.data.AggregationType
import com.example.solariotmobile.utils.ErrorResponseFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class LastTemperaturesViewModel @Inject constructor(private val temperaturesRepository: TemperaturesRepository) : ViewModel() { // private val retrofitProvider: RetrofitProvider) : ViewModel() {

    private val _loadingLastTemperatures = MutableStateFlow(false)
    val loadingLastTemperatures: StateFlow<Boolean> get() = _loadingLastTemperatures.asStateFlow()
    private val _messageLastTemperatures = MutableStateFlow("")
    val messageLastTemperatures: StateFlow<String> = _messageLastTemperatures.asStateFlow()
    private val _lastTemperatures = MutableStateFlow(emptyList<TemperatureDto>())
    val lastTemperatures: StateFlow<List<TemperatureDto>> get() = _lastTemperatures.asStateFlow()

    private val _loadingTemperatures = MutableStateFlow(false)
    val loadingTemperatures: StateFlow<Boolean> get() = _loadingTemperatures.asStateFlow()
    private val _messageTemperatures = MutableStateFlow("")
    val messageTemperatures: StateFlow<String> = _messageTemperatures.asStateFlow()
    private val _temperatures = MutableStateFlow(emptyList<TemperatureDto>())
    val temperatures: StateFlow<List<TemperatureDto>> get() = _temperatures.asStateFlow()

    fun fetchLastTemperatures() {
        _loadingLastTemperatures.value = true
        _messageLastTemperatures.value = ""
        _lastTemperatures.value = mutableListOf()
        viewModelScope.launch {
            try {
                val response = temperaturesRepository.getLastTemperatures()

                _loadingLastTemperatures.value = false

                if (response.isSuccessful) {
                    _lastTemperatures.value =
                        if (response.body() == null) emptyList() else response.body()!!
                } else {
                    _messageLastTemperatures.value = ErrorResponseFactory.createErrorMessage(response.code(), response.errorBody())
                }
            } catch (exception: Exception) {
                _loadingLastTemperatures.value = false
                _messageLastTemperatures.value = exception.message ?: "Erreur inconnue"
            }
        }
    }

    fun fetchTemperatures(aggregationType: AggregationType, startDate: LocalDateTime, endDate: LocalDateTime) {
        _loadingTemperatures.value = true
        _messageTemperatures.value = ""
        _temperatures.value = mutableListOf()

        viewModelScope.launch {
            try {
                val response = temperaturesRepository.getTemperatures(
                    aggregationType,
                    startDate,
                    endDate
                )

                _loadingTemperatures.value = false

                if (response.isSuccessful) {
                    _temperatures.value =
                        if (response.body() == null) emptyList() else response.body()!!
                } else {
                    _messageTemperatures.value = ErrorResponseFactory.createErrorMessage(response.code(), response.errorBody())
                }
            } catch (exception: Exception) {
                _loadingTemperatures.value = false
                _messageTemperatures.value = exception.message ?: "Erreur inconnue"
            }
        }
    }
}