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
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LastTemperaturesViewModel(private val retrofitProvider: RetrofitProvider) : ViewModel() {
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

                val retrofit = retrofitProvider.getRetrofit()
                val temperaturesWebService: TemperatureWebService =
                    retrofit.create(TemperatureWebService::class.java)

                val callGetLastTemperatures = temperaturesWebService.getLastTemperatures()

                callGetLastTemperatures.enqueue(object : Callback<List<TemperatureDto>> {
                    override fun onResponse(
                        call: Call<List<TemperatureDto>>,
                        response: Response<List<TemperatureDto>>
                    ) {
                        if (response.isSuccessful) {
                            _loading.value = false
                            _failure.value = false
                            _lastTemperatures.value = response.body() as List<TemperatureDto>
                        } else {
                            _failure.value = true
                            _loading.value = false
                            _message.value = response.message() ?: "Erreur inconnue"
                        }
                    }

                    override fun onFailure(call: Call<List<TemperatureDto>>, throwable: Throwable) {
                        _failure.value = true
                        _loading.value = false
                        _message.value = throwable.message ?: "Erreur inconnue"
                    }

                })
            } catch (exception: Exception) {
                _failure.value = true
                _loading.value = false
                _message.value = exception.message ?: "Erreur inconnue"
            }
    }
}

companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val application =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as IOTSolarApplication
            LastTemperaturesViewModel(application.retrofitProvider)
        }
    }
}
}