package com.example.solariotmobile.repository

import com.example.solariotmobile.data.ResistanceStateDto
import com.example.solariotmobile.domain.ApiServiceProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandRepository @Inject constructor(private val apiServiceProvider: ApiServiceProvider) {
    suspend fun getLastResistanceState() = apiServiceProvider.temperatureWebService.getLastResistanceState()
    suspend fun createResistanceState(resistanceStateDto: ResistanceStateDto) = apiServiceProvider.temperatureWebService.createResistanceState(resistanceStateDto)
}