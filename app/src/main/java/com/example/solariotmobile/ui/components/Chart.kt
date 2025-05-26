package com.example.solariotmobile.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import com.example.solariotmobile.ui.theme.SecondGreenForGradient
import java.time.format.DateTimeFormatter

@Composable
fun Chart(
    temperatures: List<ChartTemperature>,
    xAxisStepSize: Dp,
    dateFormatter: DateTimeFormatter
) {
    // Convertir les données pour YChart (en points x,y)
    val points = temperatures.mapIndexed { index, temp ->
        Point(
            x = index.toFloat(),
            y = temp.temperature.toFloat()
        )
    }

    // Déterminer min et max pour les axes
    val minTemp = temperatures.minByOrNull { it.temperature }?.temperature?.toFloat() ?: 0f
    val maxTemp =
        temperatures.maxByOrNull { it.temperature }?.temperature?.toFloat() ?: 180f
    val tempRange = ((maxTemp - minTemp) * 1.1f) // Ajouter 10% de marge
    val yMax = maxTemp + tempRange * 0.05f

    // Formatteur de date pour l'axe X
    val hoursFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Configuration de l'axe X
    val xAxisData = AxisData.Builder()
        .axisStepSize(xAxisStepSize)
        .steps(temperatures.size - 1)
        .labelData { i ->
            if (i < temperatures.size) {
                temperatures[i].dateTime.format(dateFormatter)
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
                            if (index < temperatures.size) {
                                val temp = temperatures[index]
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


    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(8.dp),
    ) {
        if (temperatures.isEmpty()) {
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
