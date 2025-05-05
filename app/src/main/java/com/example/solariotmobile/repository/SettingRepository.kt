package com.example.solariotmobile.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val SERVER_ADDRESS = stringPreferencesKey("server_address")
        val SERVER_PORT = stringPreferencesKey("server_port")
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("jwt_refresh_token")
        val NETWORK_PROTOCOL = stringPreferencesKey("network_protocol")
    }

    val getServerAddress: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[SERVER_ADDRESS] ?: ""
        }

    val getServerPort: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[SERVER_PORT] ?: ""
        }

    val getNetworkProtocol: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[NETWORK_PROTOCOL] ?: "http"
        }

    suspend fun saveServerSettings(
        serverAddress: String,
        serverPort: String,
        networkProtocol: String
    ) {
        clearToken()
        dataStore.edit { preferences ->
            preferences[SERVER_ADDRESS] = serverAddress
            preferences[SERVER_PORT] = serverPort
            preferences[NETWORK_PROTOCOL] = networkProtocol
        }
    }

    // Gestion du Token
    suspend fun saveToken(token: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.map { prefs -> prefs[TOKEN_KEY] }.firstOrNull()
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data.map { prefs -> prefs[REFRESH_TOKEN_KEY] }.firstOrNull()
    }

    suspend fun clearToken() {
        dataStore.edit {
            it.remove(TOKEN_KEY)
            it.remove(REFRESH_TOKEN_KEY)
        }
    }
}