package com.example.solariotmobile.ui.command

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CommandViewModel @Inject constructor(private val repository: CommandRepository) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _loading.asStateFlow()
    private val _failure = MutableStateFlow(false)
    val isFailure: StateFlow<Boolean> get() = _failure.asStateFlow()
    private val _message = MutableStateFlow("")
    val getMessage: StateFlow<String> get() = _message.asStateFlow()
    private val _lastResistanceState = MutableStateFlow<ResistanceStateDto?>(null)
    val lastResistanceState: StateFlow<ResistanceStateDto?> = _lastResistanceState.asStateFlow()

    fun fetchLastResistanceState() {
        viewModelScope.launch {
            _loading.value = true;
            _failure.value = false;
            _message.value = "";
            try {
                val response = repository.getLastResistanceState()

                _loading.value = false;

                if (response.isSuccessful) {
                    _lastResistanceState.value = response.body()
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
}