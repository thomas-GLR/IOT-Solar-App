package com.example.solariotmobile.ui.temperatures

import com.example.solariotmobile.api.TemperatureWebService
import javax.inject.Inject

class TemperaturesRepository @Inject constructor(private val temperatureWebService: TemperatureWebService) {
    suspend fun getLastTemperatures() = temperatureWebService.getLastTemperatures()
    suspend fun getTemperatures() = temperatureWebService.getTemperatures()
}