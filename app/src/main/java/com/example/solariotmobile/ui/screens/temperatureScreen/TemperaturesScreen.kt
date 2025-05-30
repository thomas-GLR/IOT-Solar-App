package com.example.solariotmobile.ui.screens.temperatureScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.solariotmobile.data.AggregationType
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.viewmodel.LastTemperaturesViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TemperaturesScreen(viewModel: LastTemperaturesViewModel = hiltViewModel()) {
    
    val loadingLastTemperatures by viewModel.loadingLastTemperatures.collectAsState()
    val messageLastTemperatures by viewModel.messageLastTemperatures.collectAsState()
    val lastTemperatures by viewModel.lastTemperatures.collectAsState()

    val loadingTemperatures by viewModel.loadingTemperatures.collectAsState()
    val messageTemperatures by viewModel.messageTemperatures.collectAsState()
    val temperatures by viewModel.temperatures.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchLastTemperatures()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        LastTemperatures(
            loadingLastTemperatures,
            messageLastTemperatures,
            lastTemperatures
        ) { viewModel.fetchLastTemperatures() }

        Spacer(modifier = Modifier.height(50.dp))

        TemperaturesEvolution(
            loadingTemperatures,
            messageTemperatures,
            temperatures,
            fetchData = { aggregationType: AggregationType, firstDate: LocalDateTime, endDate: LocalDateTime ->
                viewModel.fetchTemperatures(aggregationType, firstDate, endDate)
            }
        )
    }
}

@Composable
fun LandscapeDisplay(
    lastTemperatures: List<TemperatureDto>
) {
    Column(
        Modifier
            .fillMaxWidth(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterVertically
        ) {
            lastTemperatures.forEach { temperature ->
                // Creating a Canvas to draw a Circle
                TemperatureDisplay(
                    temperature = temperature.temperature,
                    readingDevice = temperature.readingDeviceName,
                    collectionDate = temperature.collectionDate
                )

                Spacer(modifier = Modifier.width(200.dp))
            }
        }
    }
}

@Composable
fun PortraitDisplay(
    lastTemperatures: List<TemperatureDto>
) {
    Row(
        Modifier.fillMaxSize(),
        Arrangement.Center,
        Alignment.CenterVertically
    ) {
        val scrollState = rememberScrollState()

        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            lastTemperatures.forEach { temperature ->
                TemperatureDisplay(
                    temperature = temperature.temperature,
                    readingDevice = temperature.readingDeviceName,
                    collectionDate = temperature.collectionDate
                )
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}

@Composable
fun TemperatureDisplay(
    temperature: Double,
    readingDevice: ReadingDeviceName,
    collectionDate: LocalDateTime
) {
    val text: String = "$temperature°C\n" +
            "${readingDevice.value}\n" +
            collectionDate.format(DateTimeFormatter.ofPattern("HH:mm"))

    Column(
        Modifier
            .fillMaxWidth(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            modifier = Modifier
                .drawWithCache {
                    val brush =
                        Brush.horizontalGradient(listOf(Color(0xFF32CD32), Color(0xFF82CF00)))
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    onDrawBehind {
                        drawCircle(
                            brush = brush,
                            // center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                            radius = 400F / 2,
                            style = Stroke(40F)
                        )
                    }
                }
        )
    }
}
