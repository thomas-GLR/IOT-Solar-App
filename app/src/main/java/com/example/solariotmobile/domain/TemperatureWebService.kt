package com.example.solariotmobile.domain

import com.example.solariotmobile.data.LoginDto
import com.example.solariotmobile.data.ResistanceStateDto
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.data.AggregationType
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDateTime

interface TemperatureWebService {
    @GET("temperatures/last-temperatures")
    suspend fun getLastTemperatures(): Response<List<TemperatureDto>>

    @GET("temperatures")
    suspend fun getTemperatures(
        @Query("aggregation_type") aggregationType: AggregationType?,
        @Query("start_date") startDate: LocalDateTime?,
        @Query("end_date") endDate: LocalDateTime?
    ): Response<List<TemperatureDto>>

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