package com.example.solariotmobile.domain

import com.example.solariotmobile.data.LoginDto
import com.example.solariotmobile.repository.SettingRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val apiService: Provider<TemperatureWebService>,
    private val settingRepository: SettingRepository
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val refreshToken = settingRepository.getRefreshToken()
            var tokenResponse = refreshToken?.let { apiService.get().refreshToken(it).body() }

            if (tokenResponse != null) {
                settingRepository.saveToken(tokenResponse.accessToken, tokenResponse.refreshToken)
                response.request().newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                    .build()
            } else {
                var username = ""
                var password = ""
                var address = ""
                var port = ""

                settingRepository.getServerUsername.combine(settingRepository.getServerPassword) { name, pass ->
                    username = name
                    password = pass
                }.first()

                settingRepository.getServerAddress.combine(settingRepository.getServerPort) { serverAddress, serverPort ->
                    address = serverAddress
                    port = serverPort
                }.first()

                val networkProtocol = runBlocking { settingRepository.getNetworkProtocol.first() }

                try {

                    val baseUrl = if (address.isNotEmpty() && port.isBlank()) {
                        "${networkProtocol}://${address}"
                    } else {
                        "${networkProtocol}://${address}:${port}"
                    }

                    val retrofit = Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val temperaturesWebService: TemperatureWebService =
                        retrofit.create(TemperatureWebService::class.java)

                    tokenResponse = temperaturesWebService.login(LoginDto(username, password)).body()

                } catch (exception: Exception) {
                    tokenResponse = null
                }
                if (tokenResponse != null) {
                    settingRepository.saveToken(tokenResponse.accessToken, tokenResponse.refreshToken)
                    response.request().newBuilder()
                        .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                        .build()
                } else {
                    null
                }
            }
        }
    }
}
