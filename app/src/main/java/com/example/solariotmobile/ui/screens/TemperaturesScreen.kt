package com.example.solariotmobile.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.solariotmobile.ui.components.FailureComponentWithRefreshButton
import com.example.solariotmobile.ui.components.LoadingComponent
import com.example.solariotmobile.viewmodel.LastTemperaturesViewModel
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TemperaturesScreen(viewModel: LastTemperaturesViewModel = hiltViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var loading by remember { mutableStateOf(false) }
    var failure by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var lastTemperatures by remember { mutableStateOf(emptyList<TemperatureDto>()) }

    viewModel.isLoading.observe(lifecycleOwner) { isLoading ->
        loading = isLoading
    }
    viewModel.isFailure.observe(lifecycleOwner) { isFailure ->
        failure = isFailure
    }
    viewModel.getMessage.observe(lifecycleOwner) { getMessage ->
        message = getMessage
    }
    viewModel.getLastTemperature.observe(lifecycleOwner) { getLastTemperature ->
        lastTemperatures = getLastTemperature
    }

    val configuration = LocalConfiguration.current

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    if (loading) {
        LoadingComponent()
    }

    if (failure) {
        FailureComponentWithRefreshButton(
            message = message,
            onButtonClick = { viewModel.fetchData() })
    }

    if (!failure && !loading) {
        if (lastTemperatures.isNotEmpty()) {
            var dates: Set<LocalDate> = lastTemperatures
                .map { lastTemperature -> lastTemperature.collectionDate.toLocalDate() }
                .toSet()

            Column(
                Modifier.fillMaxSize(),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                Box(Modifier.height(100.dp)) {
                    DisplayDates(dates)
                }

                // Text("Date de réception : ${collectionDate.format(DateTimeFormatter.ofPattern("dd / MM / yyyy"))}")
                when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        LandscapeDisplay(lastTemperatures = lastTemperatures)
                    }

                    else -> {
                        PortraitDisplay(lastTemperatures = lastTemperatures)
                    }
                }
            }
        } else {
            Text("Aucune températures à afficher")
        }
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
        Column(
            Modifier
                .fillMaxWidth(),
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
fun DisplayDates(dates: Set<LocalDate>) {
    when(dates.size) {
        0 -> Text("Aucune date récupérée")
        1 -> Text("Températures prélevées le : " + dates.first().format(DateTimeFormatter.ofPattern("dd / MM / yyyy")))
        else -> {
            var datesToDisplay: String = ""
            dates.forEach{ date ->
                if (datesToDisplay.isNotEmpty()) {
                    datesToDisplay += ", "
                }
                datesToDisplay += date.format(DateTimeFormatter.ofPattern("dd / MM / yyyy"))
            }
            Text("Les données ne possèdent pas les mêmes dates : ")
        }
    }
}

@Composable
fun TemperatureDisplay(
    temperature: Double,
    readingDevice: ReadingDeviceName,
    collectionDate: LocalDateTime
) {

    val readingDeviceName = when (readingDevice) {
        ReadingDeviceName.TOP -> "Haut"
        ReadingDeviceName.MIDDLE -> "Milieu"
        ReadingDeviceName.BOTTOM -> "Bas"
    }

    val text: String = "$temperature°C\n" +
            "$readingDeviceName\n" +
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