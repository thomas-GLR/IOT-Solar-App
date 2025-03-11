package com.example.solariotmobile.ui.command

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.solariotmobile.ui.theme.ForestGreen
import com.example.solariotmobile.ui.theme.LightGreen
import com.example.solariotmobile.ui.theme.MediumSeaGreen
import com.example.solariotmobile.ui.theme.PaleGreen
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
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (lastResistanceState.id == null) {
            Text(
                "Date de dernière modification : Aucune information disponible en base",
                color = Color.Black
            )
        } else {
            val resistanceStateInformation = if (isResistanceActive) " allumée " else " eteinte "
            val resistanceStateUpdateDate =
                lastResistanceState.lastUpdate.format(DateTimeFormatter.ofPattern("dd / MM / yyyy"))
            val resistanceStateUpdateTime =
                lastResistanceState.lastUpdate.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            Text(
                "La résistance a été $resistanceStateInformation le $resistanceStateUpdateDate à $resistanceStateUpdateTime",
                color = Color.Black
            )
        }

        Switch(
            enabled = !loading,
            checked = isResistanceActive,
            onCheckedChange = {
                viewModel.createResistanceState(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = ForestGreen,
                checkedTrackColor = LightGreen,
                uncheckedThumbColor = MediumSeaGreen,
                uncheckedTrackColor = PaleGreen
            ),
            modifier = Modifier.scale(1.5f)
        )

        if (loading) {
            LoadingComponent()
        }

        if (failure) {
            FailureComponent(message)
        }
    }
}
