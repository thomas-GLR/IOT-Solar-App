package com.example.solariotmobile.repository

import com.example.solariotmobile.domain.ApiServiceProvider
import javax.inject.Inject

class TemperaturesRepository @Inject constructor(private val apiServiceProvider: ApiServiceProvider) {
    suspend fun getLastTemperatures() = apiServiceProvider.temperatureWebService.getLastTemperatures()
}