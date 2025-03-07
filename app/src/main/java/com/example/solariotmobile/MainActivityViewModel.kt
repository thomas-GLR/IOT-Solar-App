package com.example.solariotmobile

import androidx.lifecycle.ViewModel
import com.example.solariotmobile.ui.settings.SettingRepository
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

        return isValidIpAddress(serverAddress) && isValidPort(serverPort)
    }

    private fun isValidIpAddress(ip: String): Boolean {
        return try {
            val address = InetAddress.getByName(ip)
            address.hostAddress == ip && address is java.net.Inet4Address
        } catch (e: Exception) {
            false
        }
    }

    private fun isValidPort(port: String): Boolean {
        val portNumber = port.toIntOrNull() ?: return false
        return portNumber in 0..65535
    }

}