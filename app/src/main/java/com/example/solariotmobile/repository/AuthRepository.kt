package com.example.solariotmobile.repository

import com.example.solariotmobile.data.LoginDto
import com.example.solariotmobile.domain.ApiServiceProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val apiServiceProvider: ApiServiceProvider) {
    suspend fun login(username: String, password: String) = apiServiceProvider.authService.login(LoginDto(username, password))
}