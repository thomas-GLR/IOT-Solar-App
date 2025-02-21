package com.example.solariotmobile.ui.command

import com.example.solariotmobile.api.TemperatureWebService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandRepository @Inject constructor(private val temperatureWebService: TemperatureWebService) {
    suspend fun getLastResistanceState() = temperatureWebService.getLastResistanceState()
    suspend fun createResistanceState(resistanceStateDto: ResistanceStateDto) = temperatureWebService.createResistanceState(resistanceStateDto)
}