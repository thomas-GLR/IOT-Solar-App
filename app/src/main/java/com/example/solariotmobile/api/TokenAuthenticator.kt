package com.example.solariotmobile.api

import com.example.solariotmobile.ui.settings.SettingRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiServiceProvider: Provider<TemperatureWebService>,
    private val settingRepository: SettingRepository
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val refreshToken = tokenManager.getRefreshToken()

            var tokenResponse = refreshToken?.let { apiServiceProvider.get().refreshToken(it).body() }

            if (tokenResponse != null) {
                tokenManager.saveToken(tokenResponse.accessToken, tokenResponse.refreshToken)
                response.request().newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                    .build()
            } else {
                var username = ""
                var password = ""

                settingRepository.getServerUsername.combine(settingRepository.getServerPassword) { name, pass ->
                    username = name
                    password = pass
                }.first()

                val loginDto = LoginDto(username, password)

                tokenResponse = apiServiceProvider.get().login(loginDto).body()

                if (tokenResponse != null) {
                    tokenManager.saveToken(tokenResponse.accessToken, tokenResponse.refreshToken)
                    response.request().newBuilder()
                        .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                        .build()
                } else {
                    null // Déconnexion de l'utilisateur si échec du rafraîchissement ou de la connexion
                }
            }
        }
    }
}
