package com.example.solariotmobile.domain

import com.example.solariotmobile.data.LoginDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/login")
    suspend fun login(@Body loginDto: LoginDto): Response<TokenResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<TokenResponse>
}