package com.example.solariotmobile.ui.temperatures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.solariotmobile.IOTSolarApplication
import com.example.solariotmobile.api.RetrofitProvider
import com.example.solariotmobile.api.TemperatureWebService
import com.example.solariotmobile.utils.ErrorResponseFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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