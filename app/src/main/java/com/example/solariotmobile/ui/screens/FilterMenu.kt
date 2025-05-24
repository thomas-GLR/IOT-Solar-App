package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.ui.components.ButtonWithIconAndText
import com.example.solariotmobile.ui.components.DateRangePickerModal
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import com.example.solariotmobile.ui.theme.PaleGreen
import com.example.solariotmobile.ui.theme.SecondGreenForGradient
import com.example.solariotmobile.viewmodel.TemperaturesViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

enum class MonthFilter(val value: String) {
    ALL("Tous les mois"),
    JANUARY("Janvier"),
    FEBRUARY("Février"),
    MARCH("Mars"),
    APRIL("Avril"),
    MAY("Mai"),
    JUNE("Juin"),
    JULY("Juillet"),
    AUGUST("Aout"),
    SEPTEMBER("Septembre"),
    OCTOBER("Octobre"),
    NOVEMBER("Novembre"),
    DECEMBER("Décembre")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterMenu(
    viewModel: TemperaturesViewModel
) {
    val readingDeviceByName = mapOf(
        ReadingDeviceName.TOP to "Haut",
        ReadingDeviceName.MIDDLE to "Milieu",
        ReadingDeviceName.BOTTOM to "Bas"
    )

    val readingDevices = readingDeviceByName.keys.toList()

    val selectedReadingDeviceName = remember { mutableStateListOf<ReadingDeviceName>() }
    selectedReadingDeviceName.addAll(readingDevices)

    val selectedMonth = remember { mutableStateListOf<MonthFilter>() }
    selectedMonth.addAll(MonthFilter.entries)

    val selectedDateRange = remember {
        mutableStateOf<Pair<Long?, Long?>>(null to null)
    }

    var showRangeModal by remember { mutableStateOf(false) }

    val datesRangeInformation =
        if (selectedDateRange.value != null to null && selectedDateRange.value.first != null && selectedDateRange.value.second != null)
            "${
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(selectedDateRange.value.first!!),
                    ZoneOffset.UTC
                ).toLocalDate().format(
                    DateTimeFormatter.ofPattern("dd / MM / yyyy")
                )
            } - ${
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(selectedDateRange.value.second!!),
                    ZoneOffset.UTC
                ).toLocalDate().format(
                    DateTimeFormatter.ofPattern("dd / MM / yyyy")
                )
            }" else "Aucune dates sélectionnées"

    if (showRangeModal) {
        DateRangePickerModal(
            onDateRangeSelected = {
                selectedDateRange.value = it
                viewModel.filterCollectionDateOfTemperatures(selectedDateRange.value)
                showRangeModal = false
            },
            onDismiss = { showRangeModal = false }
        )
    }

    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ButtonWithIconAndText(
            onClickButton = { showRangeModal = !showRangeModal },
            text = datesRangeInformation,
            icon = Icons.Filled.DateRange,
            iconContentDescription = "Calendrier",
        )
        MultiChoiceSegmentedButtonRow {
            readingDevices.forEachIndexed { index, readingDeviceKey ->
                SegmentedButton(
                    icon = {},
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = readingDevices.size
                    ),
                    onCheckedChange = {
                        if (readingDeviceKey in selectedReadingDeviceName) {
                            selectedReadingDeviceName.remove(readingDeviceKey)
                            viewModel.filterReadingDevices(selectedReadingDeviceName)
                        } else {
                            selectedReadingDeviceName.add(readingDeviceKey)
                            viewModel.filterReadingDevices(selectedReadingDeviceName)
                        }
                    },
                    checked = readingDeviceKey in selectedReadingDeviceName,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = FirstGreenForGradient,
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = Color.Black,
                        activeBorderColor = Color.LightGray,
                        inactiveBorderColor = Color.LightGray,
                    ),
                ) {
                    Text(
                        text = if (readingDeviceByName.containsKey(readingDeviceKey))
                            readingDeviceByName[readingDeviceKey]!!
                        else "Le label : $readingDeviceKey n'existe pas"
                    )
                }
            }
        }
        ButtonWithIconAndText(
            onClickButton = {
                viewModel.resetFilters()
                selectedDateRange.value = null to null

                selectedReadingDeviceName.clear()
                selectedReadingDeviceName.addAll(readingDevices)
            },
            text = "Réinitialiser les filtres",
            icon = Icons.Filled.Restore,
            iconContentDescription = "reset",
        )
    }
//    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text(datesRangeInformation)
//            IconButton(onClick = { showRangeModal = !showRangeModal }) {
//                Icon(
//                    imageVector = Icons.Filled.DateRange,
//                    contentDescription = "Favorite"
//                )
//            }
//            if (showRangeModal) {
//                DateRangePickerModal(
//                    onDateRangeSelected = {
//                        selectedDateRange.value = it
//                        viewModel.filterCollectionDateOfTemperatures(selectedDateRange.value)
//                        showRangeModal = false
//                    },
//                    onDismiss = { showRangeModal = false }
//                )
//            }
//        }
//        MultiChoiceSegmentedButtonRow {
//            readingDevices.forEachIndexed { index, readingDeviceKey ->
//                SegmentedButton(
//                    icon = {},
//                    shape = SegmentedButtonDefaults.itemShape(
//                        index = index,
//                        count = readingDevices.size
//                    ),
//                    onCheckedChange = {
//                        if (readingDeviceKey in selectedReadingDeviceName) {
//                            selectedReadingDeviceName.remove(readingDeviceKey)
//                            viewModel.filterReadingDevices(selectedReadingDeviceName)
//                        } else {
//                            selectedReadingDeviceName.add(readingDeviceKey)
//                            viewModel.filterReadingDevices(selectedReadingDeviceName)
//                        }
//                    },
//                    checked = readingDeviceKey in selectedReadingDeviceName,
//                    colors = SegmentedButtonDefaults.colors(
//                        activeContainerColor = FirstGreenForGradient,
//                        activeContentColor = Color.Black,
//                        inactiveContainerColor = Color.Transparent,
//                        inactiveContentColor = Color.Black,
//                    ),
//                ) {
//                    Text(
//                        text = if (readingDeviceByName.containsKey(readingDeviceKey))
//                            readingDeviceByName[readingDeviceKey]!!
//                        else "Le label : $readingDeviceKey n'existe pas"
//                    )
//                }
//            }
//        }
//        ButtonWithIconAndText(
//            onClickButton = {
//                viewModel.resetFilters()
//                selectedDateRange.value = null to null
//
//                selectedReadingDeviceName.clear()
//                selectedReadingDeviceName.addAll(readingDevices)
//            },
//            text = "Réinitialiser les filtres",
//            icon = Icons.Filled.Restore,
//            iconContentDescription = "reset",
//        )
//    }

}