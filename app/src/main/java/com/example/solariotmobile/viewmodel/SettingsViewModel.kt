package com.example.solariotmobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.data.LoginDto
import com.example.solariotmobile.domain.TemperatureWebService
import com.example.solariotmobile.domain.TokenResponse
import com.example.solariotmobile.repository.SettingRepository
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
import retrofit2.converter.gson.GsonConverterFactory
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

    fun fetchData(address: String, port: String, username: String, password: String, networkProtocol: String) {
        _loading.value = true
        _failure.value = false
        _message.value = ""

        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("${networkProtocol}://${address}:${port}")
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
                        if (response.isSuccessful) {

                            val callLogin = temperaturesWebService.loginWithCall(LoginDto(username, password))

                            callLogin.enqueue(object : Callback<TokenResponse> {
                                override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                    _loading.value = false

                                    if (response.isSuccessful) {
                                        _failure.value = false
                                        _message.value = "Identifiants corrects !"
                                    } else {
                                        _failure.value = true
                                        _message.value = "Les identifiants sont incorrects."
                                        _message.value += " Erreur : ${response.message()}"
                                    }
                                }

                                override fun onFailure(call: Call<TokenResponse>, throwable: Throwable) {
                                    _loading.value = false
                                    _failure.value = true
                                    _message.value = "Les identifiants sont incorrects."
                                    _message.value += " Erreur : ${throwable.message}"
                                }
                            })
                        } else {
                            _failure.value = true
                            _loading.value = false
                            _message.value = "Le serveur ne répond pas : ${response.message() ?: "aucun message d'erreur renvoyé"}"
                        }
                    }

                    override fun onFailure(call: Call<String>, throwable: Throwable) {
                        _failure.value = true
                        _loading.value = false
                        _message.value = "Une exception est survenue : ${throwable.message ?: "Erreur inconnue"}"
                    }
                })

//                val loginDto = LoginDto(username, password)
//
//                val response = temperatureWebService.login(loginDto)
//
//                if (!response.isSuccessful && response.code() == 401) {
//                    _failure.value = true
//                    _message.value = "Les identifiants sont incorrects"
//                } else {
//                    _failure.value = false
//                    _message.value = "Identifiants corrects !"
//                }
            } catch (exception: Exception) {
                _failure.value = true
                _loading.value = false
                _message.value = "Une exception est survenue : ${exception.message ?: "Erreur inconnue"}"
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

    val serverUsernameState: StateFlow<String> =
        settingRepository.getServerUsername.map { username -> username }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ""
            )

    val serverPasswordState: StateFlow<String> =
        settingRepository.getServerPassword.map { password -> password }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ""
            )

    val networkProtocolState: StateFlow<Boolean> =
        settingRepository.getNetworkProtocol.map { networkProtocol -> networkProtocol == "https" }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
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

    fun saveServerSettings(serverAddress: String, serverPort: String, username: String, password: String, networkProtocol: String) {

        viewModelScope.launch {
            settingRepository.saveServerSettings(serverAddress, serverPort, username, password, networkProtocol)
        }
    }

    fun getNetworkProtocol(isHttpsEnabled: Boolean): String {
        return if (isHttpsEnabled) "https" else "http"
    }
}