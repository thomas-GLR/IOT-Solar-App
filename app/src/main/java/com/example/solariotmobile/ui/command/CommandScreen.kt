package com.example.solariotmobile.ui.command

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Text
import com.example.solariotmobile.ui.components.FailureComponent
import com.example.solariotmobile.ui.components.LoadingComponent
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun CommandScreen(viewModel: CommandViewModel = hiltViewModel()) {

    val lastResistanceState by viewModel.lastResistanceState.collectAsState()
    val failure by viewModel.isFailure.collectAsState()
    val message by viewModel.getMessage.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val isResistanceActive by viewModel.isResistanceActive.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchLastResistanceState()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(50.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (lastResistanceState.id == null) {
            Text(
                "Date de dernière modification : Aucune information disponible en base",
                color = Color.Black
            )
        } else {
            Text(
                "Date de dernière modification en base : ${
                    lastResistanceState.lastUpdate
                        .format(
                            DateTimeFormatter
                                .ofPattern("dd / MM / yyyy : hh:mm:ss")
                                .withZone(ZoneOffset.UTC))
                }",
                color = Color.Black
            )
        }

        if (loading) {
            LoadingComponent()
        }

        Switch(
            enabled = !loading,
            checked = isResistanceActive,
            onCheckedChange = {
                viewModel.updateResistanceStateFromUi(it)
                viewModel.createResistanceState(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.scale(1.5f)
        )
        if (failure) {
            FailureComponent(message) { viewModel.fetchLastResistanceState() }
        }
    }
}
