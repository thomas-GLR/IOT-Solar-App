package com.example.solariotmobile.api

import com.example.solariotmobile.ui.command.ResistanceStateDto
import com.example.solariotmobile.ui.settings.HelloWorldDto
import com.example.solariotmobile.ui.temperatures.TemperatureDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface TemperatureWebService {
    @GET("temperatures/last-temperatures")
    fun getLastTemperatures(): Call<List<TemperatureDto>>

    @GET("/")
    fun getHelloWorld(): Call<String>

    @GET("resistance")
    fun getLastResistanceState(): Response<ResistanceStateDto>

    @GET("resistance")
    fun getResistancesStates(): Call<List<ResistanceStateDto>>

    @POST("resistance")
    fun createResistanceState(): Call<Unit>
}