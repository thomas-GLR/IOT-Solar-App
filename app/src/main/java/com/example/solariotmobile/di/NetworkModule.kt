package com.example.solariotmobile.di

import com.example.solariotmobile.api.TemperatureWebService
import com.example.solariotmobile.ui.settings.SettingRepository
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
    fun provideRetrofit(settingRepository: SettingRepository): Retrofit {

        val serverAddress = runBlocking { settingRepository.getServerAddress.first() }
        val serverPort = runBlocking { settingRepository.getServerPort.first() }

        val baseUrl = "http://$serverAddress:$serverPort/"

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
            .build()
    }

    @Provides
    @Singleton
    fun provideTemperatureWebService(retrofit: Retrofit): TemperatureWebService {
        return retrofit.create(TemperatureWebService::class.java)
    }
}