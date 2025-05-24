package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.ui.components.FailureWithRefresh
import com.example.solariotmobile.ui.components.Loading
import com.example.solariotmobile.ui.components.tableComponent.TableComponent
import com.example.solariotmobile.viewmodel.TemperaturesViewModel

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

    if (failure) {
        FailureWithRefresh(message) { viewModel.fetchData() }
    } else {

        // https://developer.android.com/develop/ui/compose/lists?hl=fr

        Column(modifier = Modifier.padding(25.dp)) {
            FilterMenu(viewModel)

            Spacer(modifier = Modifier.height(25.dp))

            TableComponent(temperatures, loading)
        }
    }
}
