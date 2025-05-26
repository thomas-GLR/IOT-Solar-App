package com.example.solariotmobile.ui.screens.temperatureScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.solariotmobile.data.ChartTemperature
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.data.AggregationType
import com.example.solariotmobile.ui.components.Chart
import com.example.solariotmobile.ui.components.DatePickerDocked
import com.example.solariotmobile.ui.components.FailureComponentWithRefreshButton
import com.example.solariotmobile.ui.components.LoadingComponent
import com.example.solariotmobile.ui.components.ReadingDeviceButton
import com.example.solariotmobile.ui.components.TimeRangeButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TemperaturesEvolution(
    isLoading: Boolean,
    errorMessage: String,
    temperatures: List<TemperatureDto>,
    fetchData: (aggregationType: AggregationType, firstDate: LocalDateTime, endDate: LocalDateTime) -> Unit
) {
    val sortedTemperatures = temperatures.sortedBy { it.collectionDate }

    val temperaturesByDevice = sortedTemperatures.groupBy { it.readingDeviceName }

    var selectedDevice by remember { mutableStateOf(ReadingDeviceName.TOP) }
    var selectedTimeRange by remember { mutableStateOf(AggregationType.MINUTES) }

    // Récupérer les données de température pour l'appareil sélectionné
    val selectedTemperatures = temperaturesByDevice[selectedDevice] ?: emptyList()

    val (dateFormatter, filteredTemperatures, itemsSelector) = remember(
        selectedTemperatures,
        selectedTimeRange
    ) {
        when (selectedTimeRange) {
            AggregationType.MINUTES -> {
                Triple(
                    DateTimeFormatter.ofPattern("HH:mm"),
                    selectedTemperatures.map {
                        ChartTemperature(
                            it.temperature,
                            it.collectionDate
                        )
                    },
                    listOf(
                        "Minuit",
                        "1 heure",
                        "2 heures",
                        "3 heures",
                        "4 heures",
                        "5 heures",
                        "6 heures",
                        "7 heures",
                        "8 heures",
                        "9 heures",
                        "10 heures",
                        "11 heures",
                        "12 heures",
                        "13 heures",
                        "14 heures",
                        "15 heures",
                        "16 heures",
                        "17 heures",
                        "18 heures",
                        "19 heures",
                        "20 heures",
                        "21 heures",
                        "22 heures",
                        "23 heures"
                    )
                )
            }

            AggregationType.HOURS -> {
                Triple(
                    DateTimeFormatter.ofPattern("HH:mm"),
                    transformTemperaturesDtoToChartTemperatures(
                        selectedTemperatures,
                        ChronoUnit.HOURS
                    ),
                    listOf(
                        ""
                    )
                )
            }

            AggregationType.DAYS -> {
                Triple(
                    DateTimeFormatter.ofPattern("yy"),
                    transformTemperaturesDtoToChartTemperatures(
                        selectedTemperatures,
                        ChronoUnit.DAYS
                    ),
                    listOf(
                        "Janvier",
                        "Février",
                        "Mars",
                        "Avril",
                        "Mai",
                        "Juin",
                        "Juillet",
                        "Août",
                        "Septembre",
                        "Octobre",
                        "Novembre",
                        "Décembre",
                    )
                )
            }

            AggregationType.MONTHS -> {
                Triple(
                    DateTimeFormatter.ofPattern("MM"),
                    transformTemperaturesDtoToChartTemperatures(
                        selectedTemperatures,
                        ChronoUnit.MONTHS
                    ),
                    75.dp
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        if (isLoading) {
            LoadingComponent()
        }

        if (errorMessage.isNotEmpty()) {
            FailureComponentWithRefreshButton(
                message = errorMessage,
                onButtonClick = { })
        }

        FlowRow(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ReadingDeviceButton(
                selectTopDevice = { selectedDevice = ReadingDeviceName.TOP },
                selectMiddleDevice = { selectedDevice = ReadingDeviceName.MIDDLE },
                selectBottomDevice = { selectedDevice = ReadingDeviceName.BOTTOM }
            )

            TimeRangeButton(
                selectTimeRange = { timeRangeSelected ->
                    selectedTimeRange = timeRangeSelected
                }
            )
            Box(modifier = Modifier.width(200.dp)) {
                DatePickerDocked()
            }
        }

        if (!isLoading && errorMessage.isEmpty()) {
            Chart(
                filteredTemperatures,
                75.dp,
                dateFormatter
            )
        }
    }
}

@Composable
fun TimeSelectorDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selected by remember { mutableIntStateOf(selectedIndex) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                options.forEachIndexed { index, text ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = index },
                        text = text
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

private fun transformTemperaturesDtoToChartTemperatures(
    temperatures: List<TemperatureDto>,
    chronoUnit: ChronoUnit
): List<ChartTemperature> {
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
        val averageTemperature = temperaturesDto.map { it.temperature }.average()
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