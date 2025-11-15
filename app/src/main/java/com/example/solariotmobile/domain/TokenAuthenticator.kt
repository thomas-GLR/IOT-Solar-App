package com.example.solariotmobile.domain

import com.example.solariotmobile.repository.SettingRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val authService: Provider<AuthService>,
    private val settingRepository: SettingRepository
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val refreshToken = settingRepository.getRefreshToken()
            val tokenResponse = refreshToken?.let { authService.get().refreshToken(it).body() }

            if (tokenResponse != null) {
                settingRepository.saveToken(tokenResponse.token, tokenResponse.refreshToken)
                response.request().newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.token}")
                    .build()
            } else {
                null
            }
        }
    }
}
