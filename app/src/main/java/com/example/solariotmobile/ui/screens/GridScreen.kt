package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.breens.beetablescompose.BeeTablesCompose
import com.example.solariotmobile.ui.components.FailureWithRefresh
import com.example.solariotmobile.ui.components.Loading
import com.example.solariotmobile.ui.temperatures.TemperatureDto

@Composable
fun GridScreen(viewModel: TemperaturesViewModel = hiltViewModel()) {
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

        // https://developer.android.com/develop/ui/compose/lists?hl=fr

        Column {
            FilterMenu(viewModel)
            val tableHeaders = listOf("Températures", "Date", "Sonde")

            BeeTablesCompose(
                data = temperatures,
                headerTableTitles = tableHeaders,
                dividerThickness = 0.8.dp,
                columnToIndexIncreaseWidth = 2,
                disableVerticalDividers = true,
            )
        }

//        Column {
//            FilterMenu(viewModel)
//            LazyColumn(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                items(temperatures) { temperature ->
//
//                    val readingDeviceName = when (temperature.readingDeviceName) {
//                        ReadingDeviceName.TOP -> "Haut"
//                        ReadingDeviceName.MIDDLE -> "Milieu"
//                        ReadingDeviceName.BOTTOM -> "Bas"
//                    }
//                    Row {
//                        Text("${temperature.temperature}")
//                        Text(temperature.collectionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")))
//                        Text(readingDeviceName)
//                    }
//                }
//            }
//        }
    }
}
