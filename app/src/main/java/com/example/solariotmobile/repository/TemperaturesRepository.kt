package com.example.solariotmobile.repository

import com.example.solariotmobile.domain.TemperatureWebService
import javax.inject.Inject

class TemperaturesRepository @Inject constructor(private val temperatureWebService: TemperatureWebService) {
    suspend fun getLastTemperatures() = temperatureWebService.getLastTemperatures()
}