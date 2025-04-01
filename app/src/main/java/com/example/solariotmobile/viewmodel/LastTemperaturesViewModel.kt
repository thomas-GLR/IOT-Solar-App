package com.example.solariotmobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.repository.TemperaturesRepository
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.utils.ErrorResponseFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LastTemperaturesViewModel @Inject constructor(private val temperaturesRepository: TemperaturesRepository) : ViewModel() { // private val retrofitProvider: RetrofitProvider) : ViewModel() {
    private val _loading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _loading
    private val _failure = MutableLiveData(false)
    val isFailure: LiveData<Boolean> get() = _failure
    private val _message = MutableLiveData("")
    val getMessage: LiveData<String> get() = _message
    private val _lastTemperatures = MutableLiveData<List<TemperatureDto>>()
    val getLastTemperature: LiveData<List<TemperatureDto>> get() = _lastTemperatures

    fun fetchData() {
        _loading.value = true
        _failure.value = false
        _message.value = ""
        _lastTemperatures.value = mutableListOf()
        viewModelScope.launch {
            try {
                val response = temperaturesRepository.getLastTemperatures()

                _loading.value = false

                if (response.isSuccessful) {
                    _lastTemperatures.value =
                        if (response.body() == null) emptyList() else response.body()
                } else {
                    _failure.value = true
                    _message.value = ErrorResponseFactory.createErrorMessage(response.code(), response.errorBody())
                }
            } catch (exception: Exception) {
                _failure.value = true
                _loading.value = false
                _message.value = exception.message ?: "Erreur inconnue"
            }
        }
    }
}