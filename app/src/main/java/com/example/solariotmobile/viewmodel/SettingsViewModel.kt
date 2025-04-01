package com.example.solariotmobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.domain.ApiServiceProvider
import com.example.solariotmobile.domain.TemperatureWebService
import com.example.solariotmobile.repository.SettingRepository
import com.example.solariotmobile.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val apiServiceProvider: ApiServiceProvider
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _loading

    private val _failure = MutableLiveData(false)
    val isFailure: LiveData<Boolean> get() = _failure

    private val _message = MutableLiveData("")
    val getMessage: LiveData<String> get() = _message

    private val _serverAddress = MutableStateFlow("")
    val serverAddress: StateFlow<String> = _serverAddress

    private val _serverPort = MutableStateFlow("")
    val serverPort: StateFlow<String> = _serverPort

    private val _isSecureProtocol = MutableStateFlow(false)
    val isSecureProtocol: StateFlow<Boolean> = _isSecureProtocol

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _serverAddress.value = settingRepository.getServerAddress.first()
            _serverPort.value = settingRepository.getServerPort.first()
            _isSecureProtocol.value = isSecureProtocol(settingRepository.getNetworkProtocol.first())
        }
    }

    fun testConnection(address: String, port: String, isHttpsEnabled: Boolean) {
        _loading.value = true
        _failure.value = false
        _message.value = ""

        val networkProtocol = getNetworkProtocol(isHttpsEnabled)

        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("${networkProtocol}://${NetworkUtils.getServerUrl(address, port)}")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val temperaturesWebService: TemperatureWebService =
                    retrofit.create(TemperatureWebService::class.java)

                val callGetHelloWorld = temperaturesWebService.getHelloWorld()
                callGetHelloWorld.enqueue(object : Callback<String> {
                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {
                        _loading.value = false

                        // TODO save settings if connexion is successful
                        if (response.isSuccessful) {
                            _failure.value = false
                            _message.value = "Connexion au serveur réussie !"
                        } else {
                            _failure.value = true
                            _message.value = "Le serveur ne répond pas : ${response.message() ?: "aucun message d'erreur renvoyé"}"
                        }
                    }

                    override fun onFailure(call: Call<String>, throwable: Throwable) {
                        _failure.value = true
                        _loading.value = false
                        _message.value = "Une exception est survenue : ${throwable.message ?: "Erreur inconnue"}"
                    }
                })

            } catch (exception: Exception) {
                _failure.value = true
                _loading.value = false
                _message.value = "Une exception est survenue : ${exception.message ?: "Erreur inconnue"}"
            }
        }
    }

//    val serverAddressState: StateFlow<String> =
//        settingRepository.getServerAddress.map { serverName -> serverName }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5000),
//                initialValue = ""
//            )
//
//    val serverPortState: StateFlow<String> =
//        settingRepository.getServerPort.map { serverName -> serverName }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5000),
//                initialValue = ""
//            )
//
//    val networkProtocolState: StateFlow<Boolean> =
//        settingRepository.getNetworkProtocol.map { networkProtocol -> networkProtocol == "https" }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5000),
//                initialValue = false
//            )

    fun saveServerSettings(serverAddress: String, serverPort: String, isHttpsEnabled: Boolean) {
        viewModelScope.launch {
            settingRepository.saveServerSettings(serverAddress, serverPort, getNetworkProtocol(isHttpsEnabled))

            apiServiceProvider.triggerServiceReload()
        }
    }

    private fun getNetworkProtocol(isHttpsEnabled: Boolean): String {
        return if (isHttpsEnabled) "https" else "http"
    }

    private fun isSecureProtocol(networkProtocol: String): Boolean {
        return networkProtocol == "https"
    }
}