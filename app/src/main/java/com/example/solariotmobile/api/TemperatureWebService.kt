package com.example.solariotmobile.api

import com.example.solariotmobile.ui.temperatures.TemperatureDto
import retrofit2.Call
import retrofit2.http.GET

interface TemperatureWebService {
    @GET("last-temperatures")
    fun getLastTemperatures(): Call<List<TemperatureDto>>

    @GET()
    fun getHelloWorld(): Call<String>
}