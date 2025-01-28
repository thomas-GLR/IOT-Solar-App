package com.example.solariotmobile.ui.command

import com.example.solariotmobile.api.TemperatureWebService

class CommandRepository(private val temperatureWebService: TemperatureWebService) {
    suspend fun getLastResistanceState() = temperatureWebService.getLastResistanceState()
}