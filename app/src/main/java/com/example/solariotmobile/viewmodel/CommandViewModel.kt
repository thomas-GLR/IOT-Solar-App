package com.example.solariotmobile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.data.EspParameterDto
import com.example.solariotmobile.data.EspParameters
import com.example.solariotmobile.data.ParameterDto
import com.example.solariotmobile.data.ResistanceStateDto
import com.example.solariotmobile.repository.CommandRepository
import com.example.solariotmobile.utils.ErrorResponseFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime.now
import javax.inject.Inject

@HiltViewModel
class CommandViewModel @Inject constructor(private val repository: CommandRepository) :
    ViewModel() {
    // Resistance state
    private val _loadingResistanceState = MutableStateFlow(false)
    val isLoadingResistanceState: StateFlow<Boolean> get() = _loadingResistanceState.asStateFlow()
    private val _failureResistanceState = MutableStateFlow(false)
    val isFailureResistanceState: StateFlow<Boolean> get() = _failureResistanceState.asStateFlow()
    private val _messageResistanceState = MutableStateFlow("")
    val getMessageResistanceState: StateFlow<String> get() = _messageResistanceState.asStateFlow()

    private val _lastResistanceState = MutableStateFlow(ResistanceStateDto(null, now(), false))
    val lastResistanceState: StateFlow<ResistanceStateDto> = _lastResistanceState.asStateFlow()

    // ESP parameter
    private val _loadingEspParameter = MutableStateFlow(false)
    val isLoadingEspParameter: StateFlow<Boolean> get() = _loadingEspParameter.asStateFlow()
    private val _failureEspParameter = MutableStateFlow(false)
    val isFailureEspParameter: StateFlow<Boolean> get() = _failureEspParameter.asStateFlow()
    private val _messageEspParameter = MutableStateFlow("")
    val getMessageEspParameter: StateFlow<String> get() = _messageEspParameter.asStateFlow()

    private val _espIp = MutableStateFlow("")
    val espIp: StateFlow<String> = _espIp.asStateFlow()
    private val _espPort = MutableStateFlow("")
    val espPort: StateFlow<String> = _espPort.asStateFlow()
    private val _espProtocol = MutableStateFlow("")
    val espProtocol: StateFlow<String> = _espProtocol.asStateFlow()

    private val _isResistanceActive = MutableStateFlow(false)
    val isResistanceActive: StateFlow<Boolean> = _isResistanceActive.asStateFlow()

    fun fetchLastResistanceState() {
        viewModelScope.launch {
            _loadingResistanceState.value = true
            _failureResistanceState.value = false
            _messageResistanceState.value = ""
            try {
                val response = repository.getLastResistanceState()

                _loadingResistanceState.value = false

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        _lastResistanceState.value = response.body()!!
                        _isResistanceActive.value = _lastResistanceState.value.currentState
                    }
                } else {
                    _failureResistanceState.value = true
                    _messageResistanceState.value = ErrorResponseFactory.createErrorMessage(
                        response.code(),
                        response.errorBody()
                    )
                }
            } catch (exception: Exception) {
                _loadingResistanceState.value = false;
                _failureResistanceState.value = true;
                _messageResistanceState.value =
                    if (exception.message != null) exception.message!! else "Une exception est survenue"
            }
        }
    }

    fun createResistanceState(currentState: Boolean) {
        viewModelScope.launch {
            _loadingResistanceState.value = true
            _failureResistanceState.value = false
            _messageResistanceState.value = ""
            try {
                val resistanceState = ResistanceStateDto(
                    null,
                    now(),
                    currentState
                )
                val response = repository.createResistanceState(resistanceState)
                _loadingResistanceState.value = false

                if (response.isSuccessful && response.body() != null) {
                    val resistanceStateResponse = response.body()!!

                    _lastResistanceState.value = _lastResistanceState.value.copy(
                        id = resistanceStateResponse.id,
                        lastUpdate = resistanceStateResponse.lastUpdate,
                        currentState = resistanceStateResponse.currentState
                    )
                } else {
                    _isResistanceActive.value = _lastResistanceState.value.currentState
                    _failureResistanceState.value = true
                    _messageResistanceState.value =
                        if (response.errorBody() != null) response.errorBody()!!
                            .string() else "Une erreur est survenue"
                }
            } catch (exception: Exception) {
                _isResistanceActive.value = _lastResistanceState.value.currentState
                _loadingResistanceState.value = false
                _failureResistanceState.value = true
                _messageResistanceState.value =
                    if (exception.message != null) exception.message!! else "Une exception est survenue"
            }
        }
    }

    fun fetchEspParameters() {
        viewModelScope.launch {
            _loadingEspParameter.value = true
            _failureEspParameter.value = false
            _messageEspParameter.value = ""
            try {
                val response = repository.getEspParameters()

                _loadingEspParameter.value = false

                if (response.isSuccessful && response.body() != null) {
                    val espParameterDto = response.body()!!
                    _espIp.value = espParameterDto.espIp.value
                    _espPort.value = espParameterDto.espPort.value
                    _espProtocol.value = espParameterDto.espProtocol.value
                }
                else {
                    _failureEspParameter.value = true
                    _messageEspParameter.value =
                        if (response.errorBody() != null) response.errorBody()!!
                            .string() else "Une erreur est survenue"
                }
            } catch (exception: Exception) {
                _loadingEspParameter.value = false
                _failureEspParameter.value = true
                _messageEspParameter.value =
                    if (exception.message != null) exception.message!! else "Une exception est survenue"
            }
        }
    }

    fun saveEspParameters(espIp: String, espPort: String, espProtocol: String) {
        viewModelScope.launch {
            _loadingEspParameter.value = true
            _failureEspParameter.value = false
            _messageEspParameter.value = ""
            try {
                val espParameters = EspParameterDto(
                    ParameterDto(EspParameters.ESP_IP.value, espIp),
                    ParameterDto(EspParameters.ESP_PORT.value, espPort),
                    ParameterDto(EspParameters.ESP_PROTOCOL.value, espProtocol),
                )
                val response = repository.saveEspParameters(espParameters)

                _loadingEspParameter.value = false

                if (response.isSuccessful) {
                    _espIp.value = espIp
                    _espPort.value = espPort
                    _espProtocol.value = espProtocol
                    _messageEspParameter.value = "Enregistrement réussi"
                } else {
                    _failureEspParameter.value = true
                    _messageEspParameter.value =
                        if (response.errorBody() != null) response.errorBody()!!
                            .string() else "Une erreur est survenue"
                }
            } catch (exception: Exception) {
                _loadingEspParameter.value = false
                _failureEspParameter.value = true
                _messageEspParameter.value =
                    if (exception.message != null) exception.message!! else "Une exception est survenue"
            }
        }
    }

    fun deleteMessageEspParameter() {
        _messageEspParameter.value = ""
    }

    fun updateResistanceStateFromUi(resistanceState: Boolean) {
        _isResistanceActive.value = resistanceState
    }
}