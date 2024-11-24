package com.example.solariotmobile.ui.temperatures

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TemperaturesScreen(viewModel: LastTemperaturesViewModel = viewModel(factory = LastTemperaturesViewModel.Factory)) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var loading by remember { mutableStateOf(true) }
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
        Box(modifier = Modifier.fillMaxWidth()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }

    if (failure) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(Color.Red)
                    .padding(16.dp)
            ) {
                Text("Un problème est survenue : $message")
            }
            Button(onClick = { viewModel.fetchData() }) {
                Text("Recharger la page")
            }
        }
    } else {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                LandscapeDisplay(lastTemperatures = lastTemperatures)
            }

            else -> {
                PortraitDisplay(lastTemperatures = lastTemperatures)
            }
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
                    readingDeviceName = temperature.readingDeviceName.name,
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
                    readingDeviceName = temperature.readingDeviceName.name,
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
    readingDeviceName: String,
    collectionDate: LocalDateTime
) {
    Column(
        Modifier
            .fillMaxWidth(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Text(
            text = "$temperature°C",
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
                            style = Stroke(80F)
                        )
                    }
                }
        )
        Text(text = readingDeviceName)
        Text(text = collectionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")))
    }
}