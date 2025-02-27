package com.example.solariotmobile.api

import com.example.solariotmobile.ui.command.ResistanceStateDto
import com.example.solariotmobile.ui.temperatures.TemperatureDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TemperatureWebService {
    @GET("temperatures/last-temperatures")
    suspend fun getLastTemperatures(): Response<List<TemperatureDto>>

    @GET("/")
    fun getHelloWorld(): Call<String>

    @GET("resistance")
    suspend fun getLastResistanceState(): Response<ResistanceStateDto>

    @GET("resistance")
    suspend fun getResistancesStates(): Response<List<ResistanceStateDto>>

    @POST("resistance")
    suspend fun createResistanceState(@Body resistanceStateDto: ResistanceStateDto): Response<ResistanceStateDto>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<TokenResponse>

    @POST("auth/login")
    suspend fun login(@Body loginDto: LoginDto): Response<TokenResponse>

    @POST("auth/login")
    fun loginWithCall(@Body loginDto: LoginDto): Call<TokenResponse>

}