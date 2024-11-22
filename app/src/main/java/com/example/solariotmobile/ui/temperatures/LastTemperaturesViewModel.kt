package com.example.solariotmobile.ui.temperatures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.solariotmobile.api.TemperatureWebService
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LastTemperaturesViewModel: ViewModel() {
    private val _loading =  MutableLiveData(true)
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
            val localDateTimeDeserializer = JsonDeserializer { json, _, _ ->
                LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            }
            val gson = GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.173.93:3000/temperatures/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            val temperaturesWebService: TemperatureWebService =
                retrofit.create(TemperatureWebService::class.java)

            val callGetLastTemperatures = temperaturesWebService.getLastTemperatures()

            callGetLastTemperatures.enqueue(object : Callback<List<TemperatureDto>> {
                override fun onResponse(
                    call: Call<List<TemperatureDto>>,
                    response: Response<List<TemperatureDto>>
                ) {
                    _loading.value = false
                    _failure.value = false
                    _lastTemperatures.value = response.body() as List<TemperatureDto>
                }

                override fun onFailure(call: Call<List<TemperatureDto>>, throwable: Throwable) {
                    _failure.value = true
                    _loading.value = false
                    _message.value = throwable.message ?: "Aucun message d'erreur"
                }

            })
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LastTemperaturesViewModel()
            }
        }
    }
}