package com.example.solariotmobile.ui.temperatures

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun TemperaturesScreen() {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            LandscapeDisplay()
        }

        else -> {
            PortraitDisplay()
        }
    }
}


@Composable
fun LandscapeDisplay(modifier: Modifier = Modifier) {
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
            // Creating a Canvas to draw a Circle
            TemperatureDisplay(
                temperature = 50.0,
                "HAUT"
            )

            Spacer(modifier = Modifier.width(200.dp))

            // Creating a Canvas to draw a Circle
            TemperatureDisplay(
                temperature = 45.0,
                "MILIEU"
            )

            Spacer(modifier = Modifier.width(200.dp))

            // Creating a Canvas to draw a Circle
            TemperatureDisplay(
                temperature = 80.0,
                "BAS"
            )
        }
    }
}

@Composable
fun PortraitDisplay(modifier: Modifier = Modifier) {
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
            TemperatureDisplay(
                temperature = 50.0,
                "HAUT"
            )

            Spacer(modifier = Modifier.height(150.dp))

            // Creating a Canvas to draw a Circle
            TemperatureDisplay(
                temperature = 45.0,
                "MILIEU"
            )

            Spacer(modifier = Modifier.height(150.dp))

            // Creating a Canvas to draw a Circle
            TemperatureDisplay(
                temperature = 80.0,
                "BAS"
            )
        }
    }
}

@Composable
fun TemperatureDisplay(
    temperature: Double,
    readingDeviceName: String,
    modifier: Modifier = Modifier
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
    }
}