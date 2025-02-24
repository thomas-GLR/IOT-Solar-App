package com.example.solariotmobile.api

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiService: TemperatureWebService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val newToken = apiService.refreshToken().body()?.token
            if (newToken != null) {
                tokenManager.saveToken(newToken)
                response.request().newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } else {
                null // Déconnexion de l'utilisateur si échec du rafraîchissement
            }
        }
    }
}
