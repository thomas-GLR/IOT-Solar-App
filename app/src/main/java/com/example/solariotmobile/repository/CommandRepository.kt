package com.example.solariotmobile.repository

import com.example.solariotmobile.data.EspParameterDto
import com.example.solariotmobile.data.EspParameters
import com.example.solariotmobile.data.ResistanceStateDto
import com.example.solariotmobile.domain.ApiServiceProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandRepository @Inject constructor(private val apiServiceProvider: ApiServiceProvider) {
    suspend fun getLastResistanceState() = apiServiceProvider.temperatureWebService.getLastResistanceState()
    suspend fun createResistanceState(resistanceStateDto: ResistanceStateDto) = apiServiceProvider.temperatureWebService.createResistanceState(resistanceStateDto)
    suspend fun getEspParameters() = apiServiceProvider.temperatureWebService.getEspParameters()
    suspend fun saveEspParameters(espParameterDto: EspParameterDto) = apiServiceProvider.temperatureWebService.saveEspParameters(espParameterDto)
}