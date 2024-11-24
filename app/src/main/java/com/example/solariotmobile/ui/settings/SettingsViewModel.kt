package com.example.solariotmobile.ui.settings

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.solariotmobile.IOTSolarApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingRepository: SettingRepository) : ViewModel() {

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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as IOTSolarApplication)
                SettingsViewModel(application.settingRepository)
            }
        }
    }
}