package com.example.solariotmobile.ui.screens

import android.content.res.Configuration
import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.solariotmobile.ui.components.FailureComponentWithRefreshButton
import com.example.solariotmobile.ui.components.LoadingComponent
import com.example.solariotmobile.viewmodel.LastTemperaturesViewModel
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.ui.components.Chart
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TemperaturesScreen(viewModel: LastTemperaturesViewModel = hiltViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var loading by remember { mutableStateOf(false) }
    var failure by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var lastTemperatures by remember { mutableStateOf(emptyList<TemperatureDto>()) }
    var temperatures by remember { mutableStateOf(emptyList<TemperatureDto>()) }

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
    viewModel.getTemperatures.observe(lifecycleOwner) { getTemperature ->
        temperatures = getTemperature
    }

    val configuration = LocalConfiguration.current

    LaunchedEffect(Unit) {
        viewModel.fetchData()
        viewModel.fetchTemperatures()
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
                Box(Modifier.height(25.dp)) {
                    DisplayDates(dates)
                }

                // Text("Date de réception : ${collectionDate.format(DateTimeFormatter.ofPattern("dd / MM / yyyy"))}")
//                when (configuration.orientation) {
//                    Configuration.ORIENTATION_LANDSCAPE -> {
//                        LandscapeDisplay(lastTemperatures = lastTemperatures)
//                    }
//
//                    else -> {
//                        PortraitDisplay(lastTemperatures = lastTemperatures)
//                    }
//                }

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

                        Spacer(modifier = Modifier.height(50.dp))

                        Chart(temperatures)
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
            else -> {Color.Green}
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