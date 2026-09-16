package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.solariotmobile.ui.components.FailureComponent
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import com.example.solariotmobile.viewmodel.CommandUiState
import com.example.solariotmobile.viewmodel.CommandViewModel
import java.time.format.DateTimeFormatter

@Composable
fun CommandScreen(viewModel: CommandViewModel = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.startSse()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.stopSse()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopSse()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchLastResistanceState()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is CommandUiState.Loading -> {
                CircularProgressIndicator()
            }

            is CommandUiState.Success -> {
                val successState = uiState as CommandUiState.Success
                val lastResistanceState = successState.lastResistanceState
                val isResistanceActive = successState.isResistanceActive
                val isWaitingForSseResponse = successState.isWaitingForSseResponse

                if (lastResistanceState.id == null) {
                    Text(
                        "La résistance n'est pas allumée",
                        color = Color.Black
                    )
                } else {
                    val resistanceStateInformation = if (isResistanceActive) " allumée " else " éteinte "
                    val resistanceStateUpdateDate =
                        lastResistanceState.lastUpdate.format(DateTimeFormatter.ofPattern("dd / MM / yyyy"))
                    val resistanceStateUpdateTime =
                        lastResistanceState.lastUpdate.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    Text(
                        "La résistance a été $resistanceStateInformation le $resistanceStateUpdateDate à $resistanceStateUpdateTime",
                        color = Color.Black
                    )
                }

                OutlinedButton(
                    onClick = {
                        val newState = !isResistanceActive
                        viewModel.createResistanceState(newState)
                    },
                    modifier = Modifier.size(150.dp),
                    shape = CircleShape,
                    border = BorderStroke(
                        10.dp,
                        if (isResistanceActive) FirstGreenForGradient else Color.Gray
                    ),
                    contentPadding = PaddingValues(0.dp),
                    enabled = !isWaitingForSseResponse
                ) {
                    Text(
                        if (isResistanceActive) "ON" else "OFF",
                        color = if (isResistanceActive) FirstGreenForGradient else Color.Gray
                    )
                }

                if (isWaitingForSseResponse) {
                    Text("En attente de réponse...")
                    LinearProgressIndicator(
                        modifier = Modifier.width(250.dp),
                        color = FirstGreenForGradient,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

            is CommandUiState.Error -> {
                val errorState = uiState as CommandUiState.Error
                FailureComponent(errorState.message)
            }
        }
    }
}
