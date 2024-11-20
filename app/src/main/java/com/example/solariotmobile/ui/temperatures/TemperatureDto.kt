package com.example.solariotmobile.ui.temperatures

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class TemperatureDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("value")
    val temperature: Double,
    @SerializedName("collectionDate")
    val collectionDate: LocalDateTime,
    @SerializedName("readingDeviceName")
    val readingDeviceName: ReadingDeviceName
)
