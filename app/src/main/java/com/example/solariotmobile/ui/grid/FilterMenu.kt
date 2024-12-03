package com.example.solariotmobile.ui.grid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.ui.components.DateRangePickerModal
import com.example.solariotmobile.ui.temperatures.ReadingDeviceName

@Composable
fun FilterMenu(
    viewModel: TemperaturesViewModel
) {
    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }
    val selectedReadingDeviceName = remember {
        mutableListOf<ReadingDeviceName>()
    }
    val selectedDateRange = remember {
        mutableStateOf<Pair<Long?, Long?>>(null to null)
    }

    var showRangeModal by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth()) {
        Box {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    isDropDownExpanded.value = true
                }
            ) {
                if (selectedReadingDeviceName.isEmpty()) {
                    Text(text = "Capteur")
                } else {
                    Text(text = selectedReadingDeviceName
                        .map{ readdingDevice -> readdingDevice.name }
                        .joinToString { ", " })
                }
            }
//            Button(onClick = { isDropDownExpanded.value = true }) {
//                Text("Capteur")
//            }
//            OutlinedTextField(
//                modifier = Modifier.clickable {
//                    isDropDownExpanded.value = true
//                },
//                readOnly = true,
//                value = selectedReadingDeviceName.joinToString { ", " },
//                onValueChange = {  },
//                label = { Text("Capteur") }
//            )
            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = {
                    isDropDownExpanded.value = false
                }
            ) {
                ReadingDeviceName.entries.forEach { value ->
                    DropdownMenuItem(
                        text = { Text(value.name) },
                        onClick = {
                            val selectedReadingDeviceNameContainsCurrentValue = selectedReadingDeviceName.contains(value)
                            if (selectedReadingDeviceNameContainsCurrentValue) {
                                selectedReadingDeviceName.remove(value)
                            } else {
                                selectedReadingDeviceName.add(value)
                            }
                        }
                    )
                }
            }
        }
        Button(onClick = { showRangeModal = !showRangeModal }) {
            Text("Dates")
        }
        if (showRangeModal) {
            DateRangePickerModal(
                onDateRangeSelected = {
                    selectedDateRange.value = it
                    showRangeModal = false
                },
                onDismiss = { showRangeModal = false }
            )
        }
        Button(onClick = { viewModel.filterTemperatures(selectedReadingDeviceName, selectedDateRange.value) }) {
            Text("Filtrer")
        }
    }
}