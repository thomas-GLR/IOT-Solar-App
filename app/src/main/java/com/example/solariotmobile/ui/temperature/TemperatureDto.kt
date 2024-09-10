package com.example.solariotmobile.ui.temperature

import java.time.LocalDateTime

data class TemperatureDto(
    val id: Int,
    val temperature: Float,
    val collectionDate: LocalDateTime
)
