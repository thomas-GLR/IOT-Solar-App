package com.example.solariotmobile.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.solariotmobile.IOTSolarApplication
import com.example.solariotmobile.api.RetrofitProvider
import com.example.solariotmobile.api.TemperatureWebService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {
    private val _loading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _loading
    private val _failure = MutableLiveData(false)
    val isFailure: LiveData<Boolean> get() = _failure
    private val _message = MutableLiveData("")
    val getMessage: LiveData<String> get() = _message


    fun fetchData(address: String, port: String) {
        _loading.value = true
        _failure.value = false
        _message.value = ""

        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://${address}:${port}")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
                val temperaturesWebService: TemperatureWebService =
                    retrofit.create(TemperatureWebService::class.java)

                val callGetHelloWorld = temperaturesWebService.getHelloWorld()
                callGetHelloWorld.enqueue(object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {
                        if (response.isSuccessful) {
                            _loading.value = false
                            _failure.value = false
                            _message.value = "Connexion réussie !"
                        } else {
                            _failure.value = true
                            _loading.value = false
                            _message.value = response.message() ?: "Erreur inconnue"
                        }
                    }

                    override fun onFailure(call: Call<String>, throwable: Throwable) {
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

    val serverAddressState: StateFlow<String> =
        settingRepository.getServerAddress.map { serverName -> serverName }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ""
            )
    val serverPortState: StateFlow<String> =
        settingRepository.getServerPort.map { serverName -> serverName }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ""
            )

    fun saveServerAddress(serverAddress: String) {
        viewModelScope.launch {
            settingRepository.saveServerAddress(serverAddress)
        }
    }

    fun saveServerPort(serverPort: String) {
        viewModelScope.launch {
            settingRepository.saveServerPort(serverPort)
        }
    }

    fun saveServerSettings(serverAddress: String, serverPort: String) {
        viewModelScope.launch {
            settingRepository.saveServerSettings(serverAddress, serverPort)
        }
    }

//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application = (this[APPLICATION_KEY] as IOTSolarApplication)
//                SettingsViewModel(application.settingRepository)
//            }
//        }
//    }
}