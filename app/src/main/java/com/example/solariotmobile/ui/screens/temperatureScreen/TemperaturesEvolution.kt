package com.example.solariotmobile.ui.screens.temperatureScreen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.solariotmobile.data.AggregationType
import com.example.solariotmobile.data.ChartTemperature
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.ui.components.Chart
import com.example.solariotmobile.ui.components.DatePickerDocked
import com.example.solariotmobile.ui.components.FailureComponentWithRefreshButton
import com.example.solariotmobile.ui.components.LoadingComponent
import com.example.solariotmobile.ui.components.ReadingDeviceButton
import com.example.solariotmobile.ui.components.TimeRangeButton
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import my.nanihadesuka.compose.ColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

const val FIRST_YEAR = 2025

@Composable
fun TemperaturesEvolution(
    isLoading: Boolean,
    errorMessage: String,
    temperatures: List<TemperatureDto>,
    fetchData: (aggregationType: AggregationType, firstDate: LocalDateTime, endDate: LocalDateTime) -> Unit
) {
    var selectedDevice by remember { mutableStateOf(ReadingDeviceName.TOP) }
    var selectedTimeRange by remember { mutableStateOf(AggregationType.MONTHS) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var firstDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var dateFormatter by remember { mutableStateOf(DateTimeFormatter.ofPattern("dd")) }
    var valueSelectedFromOption by remember { mutableIntStateOf(LocalDate.now().monthValue - 1) }

    // Récupérer les données de température pour l'appareil sélectionné
    var selectedTemperatures by remember { mutableStateOf<List<ChartTemperature>>(emptyList()) }

    LaunchedEffect(temperatures) {
        val sortedTemperatures = temperatures.sortedBy { it.collectionDate }
        val temperaturesByDevice = sortedTemperatures.groupBy { it.readingDeviceName }

        selectedTemperatures = temperaturesByDevice[selectedDevice]?.map {
            ChartTemperature(
                it.temperature,
                it.collectionDate
            )
        } ?: emptyList()
    }

    LaunchedEffect(selectedDevice) {
        val sortedTemperatures = temperatures.sortedBy { it.collectionDate }
        val temperaturesByDevice = sortedTemperatures.groupBy { it.readingDeviceName }

        selectedTemperatures = temperaturesByDevice[selectedDevice]?.map {
            ChartTemperature(
                it.temperature,
                it.collectionDate
            )
        } ?: emptyList()
    }

    LaunchedEffect(selectedTimeRange) {
        firstDate = null
        endDate = null
        selectedTemperatures = emptyList()

        when (selectedTimeRange) {
            AggregationType.HOURS -> {
                dateFormatter = DateTimeFormatter.ofPattern("HH:mm")
                valueSelectedFromOption = LocalTime.now().hour
            }

            AggregationType.DAYS -> {
                dateFormatter = DateTimeFormatter.ofPattern("HH:mm")
                firstDate = LocalDate.now().atStartOfDay()
                endDate = LocalDate.now().plusDays(1).atStartOfDay()
            }

            AggregationType.MONTHS -> {
                dateFormatter = DateTimeFormatter.ofPattern("dd")
                // valueSelectedFromOption est un index qui commence à 0 pour les mois.
                valueSelectedFromOption = LocalDate.now().monthValue - 1
            }

            AggregationType.YEARS -> {
                dateFormatter = DateTimeFormatter.ofPattern("MM")
                valueSelectedFromOption = getYearsFrom(FIRST_YEAR).lastIndex
            }
        }

        if (selectedTimeRange != AggregationType.DAYS) {
            val firstAndEndDate = constructStartDateAndEndDate(
                selectedTimeRange,
                valueSelectedFromOption,
                selectedDate
            )
            firstDate = firstAndEndDate.first
            endDate = firstAndEndDate.second
        }

        Log.i(
            "TemperaturesEvolution",
            "Appel fetchData avec les paramètres suivants : $selectedTimeRange, $firstDate, $endDate"
        )

        fetchData(
            selectedTimeRange,
            firstDate!!,
            endDate!!
        )
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
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
            if (selectedTimeRange != AggregationType.YEARS && selectedTimeRange != AggregationType.MONTHS) {
                DatePickerDocked(
                    defaultDate = selectedDate,
                    onDateSelected = { date ->
                        selectedDate = date

                        if (selectedTimeRange == AggregationType.DAYS) {
                            fetchData(
                                selectedTimeRange,
                                selectedDate.atStartOfDay(),
                                selectedDate.atStartOfDay().plusDays(1)
                            )
                        }
                    }
                )
            }

            if (selectedTimeRange in listOf(
                    AggregationType.HOURS,
                    AggregationType.MONTHS,
                    AggregationType.YEARS
                )
            ) {
                ButtonForDialog(
                    defaultValue = valueSelectedFromOption,
                    aggregationType = selectedTimeRange,
                    onSelectedOption = { value ->
                        valueSelectedFromOption = value

                        val firstAndEndDate =
                            constructStartDateAndEndDate(selectedTimeRange, value, selectedDate)

                        firstDate = firstAndEndDate.first
                        endDate = firstAndEndDate.second

                        fetchData(selectedTimeRange, firstDate!!, endDate!!)
                    }
                )
            }
        }

        if (isLoading) {
            LoadingComponent()
        }

        if (errorMessage.isNotEmpty()) {
            FailureComponentWithRefreshButton(
                message = errorMessage,
                onButtonClick = { })
        }
        if (!isLoading && errorMessage.isEmpty()) {
            Chart(
                selectedTemperatures,
                75.dp,
                dateFormatter
            )
        }
    }
}

private fun constructStartDateAndEndDate(
    aggregationType: AggregationType,
    value: Int,
    selectedDate: LocalDate
): Pair<LocalDateTime, LocalDateTime> {
    val firstDate: LocalDateTime
    val endDate: LocalDateTime

    when (aggregationType) {
        AggregationType.HOURS -> {
            firstDate = selectedDate.atStartOfDay().plusHours(value.toLong())
            endDate = firstDate.plusHours(1)
        }

        AggregationType.DAYS -> {
            throw Exception(
                "Erreur lors de constructStartDateAndEndDate," +
                        "il ne devrait pas être possible d'avoir la valeurs suivante de AggregationType : ${AggregationType.DAYS.name} ${AggregationType.DAYS.value}"
            )
        }

        AggregationType.MONTHS -> {
            firstDate = LocalDateTime.of(LocalDate.now().year, value + 1, 1, 0, 0)
            endDate = firstDate.plusMonths(1)
        }

        AggregationType.YEARS -> {
            val year = getYearsFrom(FIRST_YEAR)[value].toInt()
            firstDate = LocalDateTime.of(year, 1, 1, 0, 0)
            endDate = firstDate.plusYears(1)
        }
    }
    return Pair(firstDate, endDate)
}

private fun getYearsFrom(startYear: Int): List<String> {
    val currentYear = Year.now().value
    return (startYear..currentYear).map { year -> year.toString() }
}

@Composable
fun ButtonForDialog(
    defaultValue: Int,
    aggregationType: AggregationType,
    onSelectedOption: (index: Int) -> Unit
) {
    val hours = listOf(
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

    val months = listOf(
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

    val years = getYearsFrom(FIRST_YEAR)

    val (options, title, label) = when (aggregationType) {
        AggregationType.HOURS -> Triple(hours, "Sélectionner une heure", "Heure")
        // On est pas censé avoir hours pas de sélecteur pour ce dernier
        AggregationType.DAYS -> Triple(emptyList(), "Sélectionner une heure", "Heure")
        AggregationType.MONTHS -> Triple(months, "Sélectionner un mois", "Mois")
        AggregationType.YEARS -> Triple(years, "Sélectionner une année", "Année")
    }

    Log.i("ButtonForDialog", "Options : $options\ndefaultValue : $defaultValue")

    var showDialogTimeSelector by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[defaultValue]) }

    LaunchedEffect(defaultValue) {
        selectedOption = options[defaultValue]
    }

    Button(
        onClick = { showDialogTimeSelector = !showDialogTimeSelector },
        modifier = Modifier
            .width(215.dp),
        colors = buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Text(label)
            VerticalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(IntrinsicSize.Min)
            ) {
                Text(selectedOption)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select option",
                    tint = Color.LightGray
                )
            }
        }
    }

    if (showDialogTimeSelector) {
        TimeSelectorDialog(
            title = title,
            onDismiss = { showDialogTimeSelector = false },
            options = options,
            onConfirm = { selectedIndex ->
                showDialogTimeSelector = false
                selectedOption = options[selectedIndex]
                onSelectedOption(selectedIndex)
            }
        )
    }
}

@Composable
fun TimeSelectorDialog(
    title: String,
    options: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selected by remember { mutableIntStateOf(0) }
    AlertDialog(
        modifier = Modifier
            .width(400.dp)
            .fillMaxHeight(0.7f),
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            val scrollState = rememberScrollState()

            ColumnScrollbar(
                state = scrollState,
                modifier = Modifier
                    .fillMaxWidth(),
                settings = ScrollbarSettings(
                    alwaysShowScrollbar = false,
                    thumbSelectedColor = FirstGreenForGradient,
                    thumbUnselectedColor = FirstGreenForGradient
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    options.forEachIndexed { index, text ->
                        val backgroundBoxColor =
                            if (index == selected) FirstGreenForGradient else Color.Transparent
                        val textColor = if (index == selected) Color.White else Color.Black

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(35.dp)
                                .padding(30.dp, 0.dp)
                                .background(backgroundBoxColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier.clickable { selected = index },
                                text = text,
                                fontSize = 15.sp,
                                color = textColor
                            )
                        }
                    }
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