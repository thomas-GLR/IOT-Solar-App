package com.example.solariotmobile.ui.command

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime.now
import javax.inject.Inject

@HiltViewModel
class CommandViewModel @Inject constructor(private val repository: CommandRepository) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _loading.asStateFlow()
    private val _failure = MutableStateFlow(false)
    val isFailure: StateFlow<Boolean> get() = _failure.asStateFlow()
    private val _message = MutableStateFlow("")
    val getMessage: StateFlow<String> get() = _message.asStateFlow()
    private val _lastResistanceState = MutableStateFlow(ResistanceStateDto(null, now(), false))
    val lastResistanceState: StateFlow<ResistanceStateDto> = _lastResistanceState.asStateFlow()

    private val _isResistanceActive = MutableStateFlow(false)
    val isResistanceActive: StateFlow<Boolean> = _isResistanceActive.asStateFlow()

    fun fetchLastResistanceState() {
        viewModelScope.launch {
            _loading.value = true
            _failure.value = false
            _message.value = ""
            try {
                val response = repository.getLastResistanceState()

                _loading.value = false

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        _lastResistanceState.value = response.body()!!
                    }
                } else {
                    _failure.value = true
                    _message.value = if (response.errorBody() != null) response.errorBody()!!.string() else "Une erreur est survenue"
                }
            } catch (exception: Exception) {
                _loading.value = false;
                _failure.value = true;
                _message.value = if (exception.message != null) exception.message!! else "Une exception est survenue"
            }
        }
    }

    fun createResistanceState(currentState: Boolean) {
        viewModelScope.launch {
            _failure.value = false
            _message.value = ""
            try {
                val resistanceState = ResistanceStateDto(
                    null,
                    now(),
                    currentState
                )
                val response = repository.createResistanceState(resistanceState)
                _loading.value = false

                if (response.isSuccessful && response.body() != null) {
                    val resistanceStateResponse = response.body()!!

                    _lastResistanceState.value = _lastResistanceState.value.copy(
                        id = resistanceStateResponse.id,
                        lastUpdate = resistanceStateResponse.lastUpdate,
                        currentState = resistanceStateResponse.currentState
                    )
                } else {
                    _isResistanceActive.value = _lastResistanceState.value.currentState
                    _failure.value = true
                    _message.value = if (response.errorBody() != null) response.errorBody()!!.string() else "Une erreur est survenue"
                }
            } catch (exception: Exception) {
                _isResistanceActive.value = _lastResistanceState.value.currentState
                _loading.value = false;
                _failure.value = true;
                _message.value = if (exception.message != null) exception.message!! else "Une exception est survenue"
            }
        }
    }

    fun updateResistanceStateFromUi(resistanceState: Boolean) {
        _isResistanceActive.value = resistanceState
    }
}