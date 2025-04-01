package com.example.solariotmobile.di

import com.example.solariotmobile.domain.AuthInterceptor
import com.example.solariotmobile.domain.AuthService
import com.example.solariotmobile.domain.TemperatureWebService
import com.example.solariotmobile.domain.TokenAuthenticator
import com.example.solariotmobile.repository.SettingRepository
import com.example.solariotmobile.utils.NetworkUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient, settingRepository: SettingRepository): Retrofit {

        println("NetworkModule : appel")

        val serverAddressFromSettings = runBlocking { settingRepository.getServerAddress.first() }
        val serverPortFromSettings = runBlocking { settingRepository.getServerPort.first() }
        val networkProtocolFromSettings = runBlocking { settingRepository.getNetworkProtocol.first() }

        val serverAddress = serverAddressFromSettings.takeIf { it.isNotEmpty() } ?: "localhost"
        val serverPort = serverPortFromSettings.takeIf { it.isNotEmpty() } ?: "3000"
        val networkProtocol = networkProtocolFromSettings.takeIf { it.isNotEmpty() } ?: "http"

        val baseUrl = "${networkProtocol}://${NetworkUtils.getServerUrl(serverAddress, serverPort)}"

        // On veut pouvoir déserialiser les dates avec le format ISO_OFFSET_DATE_TIME pour simplifier la communication entre l'API et nestJs et le mobile
        val localDateTimeDeserializer = JsonDeserializer { json, _, _ ->
            LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
            .create()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Provides
    fun provideTemperatureWebService(retrofit: Retrofit): TemperatureWebService {
        return retrofit.create(TemperatureWebService::class.java)
    }

    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }
}