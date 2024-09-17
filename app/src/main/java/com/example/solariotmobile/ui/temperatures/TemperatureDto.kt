package com.example.solariotmobile.ui.temperatures

import java.time.LocalDateTime

data class TemperatureDto(
    val id: Int,
    val temperature: Float,
    val collectionDate: LocalDateTime
)
