package com.example.solariotmobile.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.solariotmobile.api.TemperatureWebService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val temperatureWebService: TemperatureWebService
) {

    private var host: String = ""
    private var port: String = ""
    private var username: String = ""
    private var password: String = ""

    private companion object {
        val SERVER_ADDRESS = stringPreferencesKey("server_address")
        val SERVER_PORT = stringPreferencesKey("server_port")
        val SERVER_USERNAME = stringPreferencesKey("server_username")
        val SERVER_PASSWORD = stringPreferencesKey("server_password")
    }

    suspend fun getHelloWorld() = temperatureWebService.getHelloWorld()

    val getServerAddress: Flow<String> = dataStore.data
        .map { preferences ->
            host = preferences[SERVER_ADDRESS] ?: ""
            preferences[SERVER_ADDRESS] ?: ""
        }

    val getServerPort: Flow<String> = dataStore.data
        .map { preferences ->
            port = preferences[SERVER_PORT] ?: ""
            preferences[SERVER_PORT] ?: ""
        }

    val getServerUsername: Flow<String> = dataStore.data
        .map { preferences ->
            username = preferences[SERVER_USERNAME] ?: ""
            preferences[SERVER_USERNAME] ?: ""
        }

    val getServerPassword: Flow<String> = dataStore.data
        .map { preferences ->
            password = preferences[SERVER_PASSWORD] ?: ""
            preferences[SERVER_PASSWORD] ?: ""
        }

    suspend fun saveServerAddress(serverAddress: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_ADDRESS] = serverAddress
        }
    }

    suspend fun saveServerPort(serverPort: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_PORT] = serverPort
        }
    }

    suspend fun saveServerSettings(
        serverAddress: String,
        serverPort: String,
        username: String,
        password: String
    ) {
        dataStore.edit { preferences ->
            preferences[SERVER_ADDRESS] = serverAddress
            preferences[SERVER_PORT] = serverPort
            preferences[SERVER_USERNAME] = username
            preferences[SERVER_PASSWORD] = password
        }
    }
}