package com.example.solariotmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.repository.AuthRepository
import com.example.solariotmobile.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingRepository
): ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String, stayConnected: Boolean) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = authRepository.login(username, password)

                _loginState.value = when(response.code()) {
                    200 -> LoginState.Success
                    401 -> LoginState.Error("Identifiants incorrects")
                    else -> {
                        LoginState.Error("Une erreur est survenue : [${response.code()}] ${response.message()}")
                    }
                }

                if (_loginState.value == LoginState.Success && response.body() != null) {
                    val token = response.body()!!.accessToken
                    val refreshToken = response.body()!!.refreshToken

                    settingsRepository.saveToken(token, refreshToken)

                    if (stayConnected) {
                        settingsRepository.saveCredentials(username, password)
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Erreur de connexion")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
