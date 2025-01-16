package com.example.solariotmobile.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        mutableStateOf(emptyList<ReadingDeviceName>())
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
                Text(text = "Capteur")
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
                    selectedReadingDeviceName.value
                    DropdownMenuItem(
                        text = { Text(value.name) },
                        modifier = if (selectedReadingDeviceName.value.contains(value)) Modifier.background(Color.LightGray) else Modifier,
                        onClick = {
                            selectedReadingDeviceName.value = selectedReadingDeviceName.value.toMutableList().apply {
                                if (contains(value)) remove(value) else add(value)
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
        Button(onClick = { viewModel.filterTemperatures(selectedReadingDeviceName.value, selectedDateRange.value) }) {
            Text("Filtrer")
        }
    }
}