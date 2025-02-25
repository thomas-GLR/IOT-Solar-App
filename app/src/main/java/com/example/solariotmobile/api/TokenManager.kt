package com.example.solariotmobile.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_prefs"
)

class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val TOKEN_DATE = stringPreferencesKey("jwt_date")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("jwt_refresh_token")
    }

    suspend fun saveToken(token: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[TOKEN_DATE] = now().toString()
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.map { prefs -> prefs[TOKEN_KEY] }.firstOrNull()
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data.map { prefs -> prefs[REFRESH_TOKEN_KEY] }.firstOrNull()
    }

    suspend fun isTokenValid(): Boolean {
        val tokenDate = dataStore.data.map { prefs -> prefs[TOKEN_DATE] }.firstOrNull()
        if (tokenDate != null) {
            return now().isBefore(LocalDateTime.parse(tokenDate).plusMinutes(9))
        }

        // Je pars du principe que si je ne trouve pas la date alors le token n'est pas valide
        return false
    }

    suspend fun clearToken() {
        dataStore.edit {
            it.remove(TOKEN_KEY)
            it.remove(TOKEN_DATE)
            it.remove(REFRESH_TOKEN_KEY)
        }
    }
}
