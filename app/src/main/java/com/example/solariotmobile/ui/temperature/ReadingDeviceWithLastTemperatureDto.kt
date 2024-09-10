package com.example.solariotmobile.ui.temperature

data class ReadingDeviceWithLastTemperatureDto (
    val id: Int,
    val name: String,
    val lastTemperature: TemperatureDto
)