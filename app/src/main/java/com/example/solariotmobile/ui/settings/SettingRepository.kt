package com.example.solariotmobile.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private var host: String = ""
    private var port: String = ""

    private companion object {
        val SERVER_ADDRESS = stringPreferencesKey("server_address")
        val SERVER_PORT = stringPreferencesKey("server_port")
    }

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

    suspend fun saveServerSettings(serverAddress: String, serverPort: String) {
        dataStore.edit { preferences ->
            preferences[SERVER_ADDRESS] = serverAddress
            preferences[SERVER_PORT] = serverPort
        }
    }
}