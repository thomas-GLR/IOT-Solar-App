package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.ui.components.DateRangePickerModal
import com.example.solariotmobile.viewmodel.TemperaturesViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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

    val selectedDateRange = remember {
        mutableStateOf<Pair<Long?, Long?>>(null to null)
    }

    var showRangeModal by remember { mutableStateOf(false) }

    val datesRangeInformation = if (selectedDateRange.value != null to null && selectedDateRange.value.first != null && selectedDateRange.value.second != null)
        "${LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedDateRange.value.first!!), ZoneOffset.UTC).toLocalDate().format(
        DateTimeFormatter.ofPattern("dd / MM / yyyy"))} - ${LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedDateRange.value.second!!), ZoneOffset.UTC).toLocalDate().format(
        DateTimeFormatter.ofPattern("dd / MM / yyyy"))}" else "Aucune dates sélectionnées"

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(datesRangeInformation)
            IconButton(onClick = { showRangeModal = !showRangeModal }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Favorite"
                )
            }
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
            Button(
                onClick = {
                    viewModel.resetFilters()
                    selectedDateRange.value = null to null

                    selectedReadingDeviceName.clear()
                    selectedReadingDeviceName.addAll(readingDevices)
                }
            ) {
                Text("Réinitialiser les filtres")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            MultiChoiceSegmentedButtonRow {
                readingDevices.forEachIndexed { index, readingDeviceKey ->
                    SegmentedButton(
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
                        checked = readingDeviceKey in selectedReadingDeviceName
                    ) {
                        Text(
                            if (readingDeviceByName.containsKey(readingDeviceKey))
                                readingDeviceByName[readingDeviceKey]!!
                            else "Le label : $readingDeviceKey n'existe pas"
                        )
                    }
                }
            }
        }
    }

}