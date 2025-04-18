package com.example.solariotmobile.di

import com.example.solariotmobile.domain.AuthInterceptor
import com.example.solariotmobile.domain.TemperatureWebService
import com.example.solariotmobile.domain.TokenAuthenticator
import com.example.solariotmobile.repository.SettingRepository
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
        val serverAddress = runBlocking { settingRepository.getServerAddress.first() }
        val serverPort = runBlocking { settingRepository.getServerPort.first() }
        val networkProtocol = runBlocking { settingRepository.getNetworkProtocol.first() }

        val fallbackAddress = "localhost"
        val fallbackPort = "3000"
        val fallbackNetworkProtocol = "http"

        var baseUrl: String

        if (serverAddress.isNotEmpty() && serverPort.isEmpty()) {
            baseUrl =
                "${networkProtocol.takeIf { it.isNotEmpty() } ?: fallbackNetworkProtocol}://${serverAddress}/"
        } else {
            baseUrl =
                "${networkProtocol.takeIf { it.isNotEmpty() } ?: fallbackNetworkProtocol}://${serverAddress.takeIf { it.isNotEmpty() } ?: fallbackAddress}:${serverPort.takeIf { it.isNotEmpty() } ?: fallbackPort}/"
        }

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
    @Singleton
    fun provideTemperatureWebService(retrofit: Retrofit): TemperatureWebService {
        return retrofit.create(TemperatureWebService::class.java)
    }
}