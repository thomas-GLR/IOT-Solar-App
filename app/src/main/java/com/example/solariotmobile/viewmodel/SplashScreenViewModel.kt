package com.example.solariotmobile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.solariotmobile.repository.AuthRepository
import com.example.solariotmobile.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingRepository: SettingRepository
): ViewModel() {
    suspend fun getCredential(): Pair<String?, String?> {
        return Pair(settingRepository.getUsername(), settingRepository.getPassword())
    }

    suspend fun login(username: String, password: String): Boolean {
        try {
            val response = authRepository.login(username, password)

            if (response.code() == 200 && response.body() != null) {
                val token = response.body()!!.accessToken
                val refreshToken = response.body()!!.refreshToken

                settingRepository.saveToken(token, refreshToken)

                return true
            }
        } catch (e: Exception) {
            Log.e("SplashScreenViewModel", "Une erreur est survenue lors de la tentative de connexion automatique : " + e.message)
        }

        return false
    }
}