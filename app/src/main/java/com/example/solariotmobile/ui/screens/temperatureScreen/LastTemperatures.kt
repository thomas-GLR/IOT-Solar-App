package com.example.solariotmobile.ui.screens.temperatureScreen

import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.ui.components.FailureComponentWithRefreshButton
import com.example.solariotmobile.ui.components.LoadingComponent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LastTemperatures(
    isLoading: Boolean,
    errorMessage: String,
    lastTemperatures: List<TemperatureDto>,
    fetchData: () -> Unit
) {
    if (isLoading) {
        LoadingComponent()
    }

    if (errorMessage.isNotEmpty()) {
        FailureComponentWithRefreshButton(
            message = errorMessage,
            onButtonClick = { fetchData() })
    }

    if (!isLoading && errorMessage.isEmpty()) {
        if (lastTemperatures.isNotEmpty()) {
            val dates: Set<LocalDate> = lastTemperatures
                .map { lastTemperature -> lastTemperature.collectionDate.toLocalDate() }
                .toSet()

            Column(
                Modifier.fillMaxSize(),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                Box(Modifier.height(25.dp)) {
                    DisplayDates(dates)
                }

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
                        FlowRow(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            lastTemperatures.forEach { temperature ->
                                TemperatureCard(
                                    temperature = temperature.temperature,
                                    readingDevice = temperature.readingDeviceName,
                                    collectionDate = temperature.collectionDate
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text("Aucune températures à afficher")
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
fun TemperatureCard(
    temperature: Double,
    readingDevice: ReadingDeviceName,
    collectionDate: LocalDateTime
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .size(200.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            CustomThreeQuarterCircle(
                temperatureValue = temperature,
                modifier = Modifier.size(100.dp)
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("${readingDevice.value} - ${collectionDate.format(DateTimeFormatter.ofPattern("HH:mm"))}")
        }
    }
}

@Composable
fun CustomThreeQuarterCircle(
    temperatureValue: Double,
    modifier: Modifier = Modifier,
    trackColor: Color = Color.LightGray,
    strokeWidth: Dp = 15.dp
) {
    // La sonde va de -50 à 180. Le début du cercle est donc de -50 et la fin à 180 il y a un total de 230
//    val progress = temperatureValue / 230

    Canvas(modifier = modifier) {
        val minValue = -50f
        val maxValue = 180f
        val sweepMax = 270f

        val progress = ((temperatureValue.toFloat() - minValue) / (maxValue - minValue)).coerceIn(
            0f, 1f)
        val currentSweep = sweepMax * progress

        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        val startAngle = 135f

        val progressColor = when (temperatureValue) {
            in -50f..-10f -> Color.Blue
            in -9f..10f -> Color(0, 128, 255)
            in 11f..50f -> Color(255, 128, 0)
            in 51f..180f -> Color.Red
            else -> {
                Color.Green}
        }

        // Draw background arc (track)
        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = sweepMax,
            useCenter = false,
            style = stroke
        )

        // Draw progress arc
        drawArc(
            color = progressColor,
            startAngle = startAngle,
            sweepAngle = currentSweep,
            useCenter = false,
            style = stroke
        )

        drawContext.canvas.nativeCanvas.apply {
            val text = "$temperatureValue°C"
            val paint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 35f
                textAlign = Paint.Align.CENTER
            }
            // Center the text
            drawText(
                text,
                size.width / 2,
                size.height / 2 - (paint.descent() + paint.ascent()) / 2,
                paint
            )
        }
    }
}