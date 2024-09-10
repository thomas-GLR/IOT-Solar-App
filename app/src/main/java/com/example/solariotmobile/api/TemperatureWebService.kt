package com.example.solariotmobile.api

import com.example.solariotmobile.ui.temperature.ReadingDeviceWithLastTemperatureDto
import retrofit2.http.GET

interface TemperatureWebService {
    @GET("http://127.0.0.1:8000/api/temperature/get-last-temperatures")
    fun getLastTemperatures(): List<ReadingDeviceWithLastTemperatureDto>
}