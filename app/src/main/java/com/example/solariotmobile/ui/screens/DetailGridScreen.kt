package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.ui.components.FailureWithRefresh
import com.example.solariotmobile.ui.components.tableComponent.TableComponent
import com.example.solariotmobile.ui.components.tableComponent.TextCellComponent
import com.example.solariotmobile.viewmodel.GridDetailViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DetailGridScreen(
    date: LocalDate,
    deviceName: ReadingDeviceName,
    viewModel: GridDetailViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoadingDetail.collectAsState()
    val isFailure by viewModel.isFailureDetail.collectAsState()
    val message by viewModel.getMessageDetail.collectAsState()

    val temperatures by viewModel.temperaturesDetail.collectAsState()

    LaunchedEffect(date, deviceName) {
        viewModel.fetchDetailTemperatures(date, deviceName)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(25.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp)
                .background(Color.Transparent, RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Information"
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                "Les températures affichés correspondent à la date du ${
                    date.format(
                        DateTimeFormatter.ofPattern("dd / MM / yyyy")
                    )
                }"
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        if (isFailure) {
            FailureWithRefresh(message) { viewModel.fetchDetailTemperatures(date, deviceName) }
        }

        TableComponent(
            temperatures = temperatures,
            isLoading = isLoading,
            headers = listOf("Température", "Heure"),
            contentRow = { temperature ->
                DetailRowContent(temperature)
            })
    }
}

@Composable
fun DetailRowContent(
    temperature: TemperatureDto
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val modifier = Modifier.weight(2f)
        val height = 45.dp

        TextCellComponent(
            "${BigDecimal(temperature.temperature).setScale(2, RoundingMode.HALF_UP)}",
            modifier,
            height
        )
        TextCellComponent(
            temperature.collectionDate.format(
                DateTimeFormatter.ofPattern(
                    "HH:mm:ss"
                )
            ), modifier, height
        )
    }
}
