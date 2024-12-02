package com.example.solariotmobile.ui.grid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.ui.temperatures.TemperatureDto
import kotlinx.coroutines.launch

class TemperaturesViewModel: ViewModel() {
    private val _loading =  MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _loading
    private val _failure = MutableLiveData(false)
    val isFailure: LiveData<Boolean> get() = _failure
    private val _message = MutableLiveData("")
    val getMessage: LiveData<String> get() = _message
    private val _temperatures = MutableLiveData<List<TemperatureDto>>()
    val getTemperature: LiveData<List<TemperatureDto>> get() = _temperatures

    fun fetchData() {
        _loading.value = true
        _failure.value = false
        _message.value = ""
        _temperatures.value = mutableListOf()

        viewModelScope.launch {

        }
    }
}