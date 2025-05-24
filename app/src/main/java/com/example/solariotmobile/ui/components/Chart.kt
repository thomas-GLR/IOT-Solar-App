package com.example.solariotmobile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.solariotmobile.data.ChartTemperature
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import com.example.solariotmobile.ui.theme.SecondGreenForGradient
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun Chart(
    temperatures: List<TemperatureDto>
) {
    val sortedTemperatures = temperatures.sortedBy { it.collectionDate }

    val temperaturesByDevice = sortedTemperatures.groupBy { it.readingDeviceName }

    var selectedDevice by remember { mutableStateOf(ReadingDeviceName.TOP) }
    var selectedTimeRange by remember { mutableStateOf(TimeRange.MINUTES) }


    // Récupérer les données de température pour l'appareil sélectionné
    val selectedTemperatures = temperaturesByDevice[selectedDevice] ?: emptyList()

    val (dateFormatter, filteredTemperatures, XAxisStepSize) = remember(
        selectedTemperatures,
        selectedTimeRange
    ) {
        when (selectedTimeRange) {
            TimeRange.MINUTES -> {
                Triple(
                    DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"),
                    selectedTemperatures.map {
                        ChartTemperature(
                            it.temperature,
                            it.collectionDate
                        )
                    },
                    125.dp
                )
            }

            TimeRange.HOURS -> {
                Triple(
                    DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"),
                    transformTemperaturesDtoToChartTemperatures(
                        selectedTemperatures,
                        ChronoUnit.HOURS
                    ),
                    125.dp
                )
            }

            TimeRange.DAYS -> {
                Triple(
                    DateTimeFormatter.ofPattern("dd/MM/yy"),
                    transformTemperaturesDtoToChartTemperatures(
                        selectedTemperatures,
                        ChronoUnit.DAYS
                    ),
                    90.dp
                )
            }

            TimeRange.MONTHS -> {
                Triple(
                    DateTimeFormatter.ofPattern("MM/yy"),
                    transformTemperaturesDtoToChartTemperatures(
                        selectedTemperatures,
                        ChronoUnit.MONTHS
                    ),
                    75.dp
                )
            }
        }
    }

    // Convertir les données pour YChart (en points x,y)
    val points = filteredTemperatures.mapIndexed { index, temp ->
        Point(
            x = index.toFloat(),
            y = temp.temperature.toFloat()
        )
    }

    // Déterminer min et max pour les axes
    val minTemp = filteredTemperatures.minByOrNull { it.temperature }?.temperature?.toFloat() ?: 0f
    val maxTemp = filteredTemperatures.maxByOrNull { it.temperature }?.temperature?.toFloat() ?: 180f
    val tempRange = ((maxTemp - minTemp) * 1.1f) // Ajouter 10% de marge
    val yMax = maxTemp + tempRange * 0.05f

    // Formatteur de date pour l'axe X
    val hoursFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Configuration de l'axe X
    val xAxisData = AxisData.Builder()
        .axisStepSize(XAxisStepSize)
        .steps(filteredTemperatures.size - 1)
        .labelData { i ->
            if (i < filteredTemperatures.size) {
                filteredTemperatures[i].dateTime.format(dateFormatter)
            } else {
                ""
            }
        }
        .labelAndAxisLinePadding(20.dp)
        .axisLabelAngle(0f) // Incliner les étiquettes pour éviter le chevauchement
        .shouldDrawAxisLineTillEnd(true)
        .build()

    // Configuration de l'axe Y
    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelData { i ->
            val step = ((yMax - 0) / 5).toInt()
            val value = 0 + i * step
            "$value°C"
        }
        .labelAndAxisLinePadding(25.dp)
        .shouldDrawAxisLineTillEnd(true)
        .build()

    // Configuration des lignes du graphique
    val lineChartData = LineChartData(
        backgroundColor = Color.Transparent,
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = points,
                    LineStyle(
                        color = SecondGreenForGradient,
                        width = 3f
                    ),
                    IntersectionPoint(
                        color = FirstGreenForGradient,
                        radius = 5.dp
                    ),
                    SelectionHighlightPoint(radius = 6.dp),
                    ShadowUnderLine(
                        alpha = 0.3f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                FirstGreenForGradient.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    ),
                    SelectionHighlightPopUp(
                        popUpLabel = { x, _ ->
                            val index = x.toInt()
                            if (index < filteredTemperatures.size) {
                                val temp = filteredTemperatures[index]
                                "${temp.temperature}°C"
                            } else {
                                ""
                            }
                        }
                    )
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(
            enableVerticalLines = true,
            enableHorizontalLines = true
        )
    )

//    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(8.dp)
//            .verticalScroll(scrollState)
    ) {
        ReadingDeviceButton(
            selectTopDevice = { selectedDevice = ReadingDeviceName.TOP },
            selectMiddleDevice = { selectedDevice = ReadingDeviceName.MIDDLE },
            selectBottomDevice = { selectedDevice = ReadingDeviceName.BOTTOM }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TimeRangeButton(
            selectTimeRange = {
                timeRangeSelected -> selectedTimeRange = timeRangeSelected
            }
        )

        ElevatedCard (
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(8.dp),
        ) {
            if (filteredTemperatures.isEmpty()) {
                Text(
                    text = "Aucune donnée de température disponible",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            } else {
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    lineChartData = lineChartData
                )
            }
        }
    }
}

// Énumération pour les plages de temps
enum class TimeRange(val value: String) {
    MINUTES("Minutes"),
    HOURS("Heures"),
    DAYS("Jours"),
    MONTHS("Mois")
}

private fun transformTemperaturesDtoToChartTemperatures(temperatures: List<TemperatureDto>, chronoUnit: ChronoUnit): List<ChartTemperature> {
    val temperaturesByHours: Map<LocalDateTime, List<TemperatureDto>> = temperatures.groupBy {
        if (chronoUnit != ChronoUnit.MONTHS) {
            it.collectionDate.truncatedTo(chronoUnit)
        } else {
            it.collectionDate.withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        }
    }

    val chartTemperatures: MutableList<ChartTemperature> = mutableListOf()
    temperaturesByHours.keys.forEach { collectionDate ->
        val temperaturesDto = temperaturesByHours[collectionDate]!!
        val averageTemperature = temperaturesDto.map{ it.temperature }.average()
        chartTemperatures.add(
            ChartTemperature(
                averageTemperature,
                // On peut prendre le premier car ils ont tous la même date / heure
                temperaturesDto.first().collectionDate
            )
        )
    }
    chartTemperatures.sortedBy { it.dateTime }
    return chartTemperatures.toList()
}
