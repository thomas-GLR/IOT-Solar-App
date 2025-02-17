package com.example.solariotmobile.api

import com.example.solariotmobile.ui.settings.SettingRepository
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Deprecated("Now using Hilt")
class RetrofitProvider(private val settingRepository: SettingRepository) {
    private var retrofit: Retrofit? = null

    suspend fun getRetrofit(): Retrofit {
        val baseUrl =
            settingRepository.getServerAddress.combine(settingRepository.getServerPort) { address, port ->
                "http://$address:$port/"
            }.first()

        if (retrofit != null && retrofit!!.baseUrl().toString() == baseUrl) {
            return retrofit!!
        }

        val localDateTimeDeserializer = JsonDeserializer { json, _, _ ->
            LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
            .create()

        // Sinon, créer une nouvelle instance de Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit!!
    }
}