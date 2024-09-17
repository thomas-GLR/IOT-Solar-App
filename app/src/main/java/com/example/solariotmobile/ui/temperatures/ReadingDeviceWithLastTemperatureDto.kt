package com.example.solariotmobile.ui.temperatures

data class ReadingDeviceWithLastTemperatureDto (
    val id: Int,
    val name: String,
    val lastTemperature: TemperatureDto
)