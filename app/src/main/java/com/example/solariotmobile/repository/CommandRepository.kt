package com.example.solariotmobile.repository

import com.example.solariotmobile.domain.TemperatureWebService
import com.example.solariotmobile.data.ResistanceStateDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandRepository @Inject constructor(private val temperatureWebService: TemperatureWebService) {
    suspend fun getLastResistanceState() = temperatureWebService.getLastResistanceState()
    suspend fun createResistanceState(resistanceStateDto: ResistanceStateDto) = temperatureWebService.createResistanceState(resistanceStateDto)
}