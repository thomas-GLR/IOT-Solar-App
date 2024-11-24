package com.example.solariotmobile.api

import com.example.solariotmobile.ui.settings.SettingRepository
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RetrofitInstance {
    private lateinit var settingRepository: SettingRepository

    fun initialize(repository: SettingRepository) {
        settingRepository = repository
    }

    private val retrofit: Retrofit by lazy {
        val localDateTimeDeserializer = JsonDeserializer { json, _, _ ->
            LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
            .create()
        Retrofit.Builder()
            .baseUrl(settingRepository.getUrl())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    val temperatureWebService: TemperatureWebService by lazy {
        retrofit.create(TemperatureWebService::class.java)
    }
}

//class RetrofitService constructor(val settingRepository: SettingRepository) {
//
//    private fun build(): Retrofit {
//        val localDateTimeDeserializer = JsonDeserializer { json, _, _ ->
//            LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
//        }
//
//        val gson = GsonBuilder()
//            .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
//            .create()
//        return Retrofit.Builder()
//            .baseUrl(settingRepository.getUrl())
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//    }
//
//    fun temperaturesWebService(): TemperatureWebService {
//        return build().create(TemperatureWebService::class.java)
//    }
//}