package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.solariotmobile.ui.components.DateRangePickerModal
import com.example.solariotmobile.ui.components.FailureWithRefresh
import com.example.solariotmobile.ui.components.Loading
import com.example.solariotmobile.ui.temperatures.ReadingDeviceName
import com.example.solariotmobile.ui.temperatures.TemperatureDto
import java.time.format.DateTimeFormatter

@Composable
fun GridScreen(viewModel: TemperaturesViewModel = viewModel(factory = TemperaturesViewModel.Factory)) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var loading by remember { mutableStateOf(false) }
    var failure by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
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
    viewModel.getTemperaturesFiltered.observe(lifecycleOwner) { getTemperatures ->
        temperatures = getTemperatures
    }

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    if (loading) {
        Loading()
    }
    if (failure) {
        FailureWithRefresh(message) { viewModel.fetchData() }
    } else {

        val isDropDownExpanded = remember {
            mutableStateOf(false)
        }
        val selectedReadingDeviceName = remember {
            mutableStateOf<ReadingDeviceName?>(null)
        }
        var selectedDateRange = remember {
            mutableStateOf<Pair<Long?, Long?>>(null to null)
        }

        var showRangeModal by remember { mutableStateOf(false) }

        Column {
            Row {
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
                                    isDropDownExpanded.value = false
                                    selectedReadingDeviceName.value = value
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
                Button(onClick = { viewModel.filterTemperaturesByReadingName(ReadingDeviceName.TOP) }) {
                    Text("Filtrer par capteur")
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(temperatures) { temperature ->

                    val readingDeviceName = when (temperature.readingDeviceName) {
                        ReadingDeviceName.TOP -> "Haut"
                        ReadingDeviceName.MIDDLE -> "Milieu"
                        ReadingDeviceName.BOTTOM -> "Bas"
                    }
                    Row {
                        Text("${temperature.temperature}")
                        Text(temperature.collectionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")))
                        Text(readingDeviceName)
                    }
                }
            }
        }
    }
}
