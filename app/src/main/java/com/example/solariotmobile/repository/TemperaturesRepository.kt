package com.example.solariotmobile.repository

import com.example.solariotmobile.data.AggregationType
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.domain.ApiServiceProvider
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class TemperaturesRepository @Inject constructor(private val apiServiceProvider: ApiServiceProvider) {
    suspend fun getLastTemperatures() = apiServiceProvider.temperatureWebService.getLastTemperatures()
    suspend fun getTemperatures(
        aggregationType: AggregationType? = null,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null) = apiServiceProvider.temperatureWebService.getTemperatures(aggregationType, startDate, endDate)
    suspend fun getDetailTemperatures(firstDate: LocalDateTime, endDate: LocalDateTime,readingDeviceName: ReadingDeviceName) = apiServiceProvider.temperatureWebService.getDetailTemperatures(firstDate, endDate, readingDeviceName)
}