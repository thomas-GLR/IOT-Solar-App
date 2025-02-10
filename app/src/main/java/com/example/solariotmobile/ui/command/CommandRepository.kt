package com.example.solariotmobile.ui.command

import com.example.solariotmobile.api.TemperatureWebService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class CommandRepository @Inject constructor(private val temperatureWebService: TemperatureWebService) {
    suspend fun getLastResistanceState() = temperatureWebService.getLastResistanceState()
}