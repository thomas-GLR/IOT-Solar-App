package com.example.solariotmobile.ui.grid

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
import com.example.solariotmobile.ui.temperatures.ReadingDeviceName
import com.example.solariotmobile.ui.temperatures.TemperatureDto
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class TemperaturesViewModel(private val retrofitProvider: RetrofitProvider): ViewModel() {
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

                val retrofit = retrofitProvider.getRetrofit()
                val temperaturesWebService: TemperatureWebService =
                    retrofit.create(TemperatureWebService::class.java)

                val callGetLastTemperatures = temperaturesWebService.getTemperatures()

                callGetLastTemperatures.enqueue(object : Callback<List<TemperatureDto>> {
                    override fun onResponse(
                        call: Call<List<TemperatureDto>>,
                        response: Response<List<TemperatureDto>>
                    ) {
                        if (response.isSuccessful) {
                            _loading.value = false
                            _failure.value = false
                            _temperatures.value = response.body() as List<TemperatureDto>
                            _temperaturesFiltered.value = response.body() as List<TemperatureDto>
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as IOTSolarApplication
                TemperaturesViewModel(application.retrofitProvider)
            }
        }
    }
}