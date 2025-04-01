package com.example.solariotmobile

import androidx.lifecycle.ViewModel
import com.example.solariotmobile.repository.SettingRepository
import com.example.solariotmobile.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val settingRepository: SettingRepository) :
    ViewModel() {
    fun isValidConnexionInformation(): Boolean {

        val serverAddress = runBlocking { settingRepository.getServerAddress.first() }
        val serverPort = runBlocking { settingRepository.getServerPort.first() }

        // Si on utilise un nom de domaine plutot qu'un port
        if (NetworkUtils.isDomainName(serverAddress)) {
            return true
        }

        return NetworkUtils.isValidIpAddress(serverAddress) && NetworkUtils.isValidPort(serverPort)
    }
}