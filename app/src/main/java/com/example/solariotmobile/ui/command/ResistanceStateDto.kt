package com.example.solariotmobile.ui.command

import java.time.LocalDateTime

data class ResistanceStateDto(val id: Int?, val lastUpdate: LocalDateTime, val currentState: Boolean)