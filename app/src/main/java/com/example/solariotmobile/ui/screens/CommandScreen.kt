package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.solariotmobile.ui.components.ButtonWithIconAndText
import com.example.solariotmobile.ui.components.FailureComponent
import com.example.solariotmobile.ui.components.LoadingComponent
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import com.example.solariotmobile.ui.theme.ForestGreen
import com.example.solariotmobile.ui.theme.LightGreen
import com.example.solariotmobile.ui.theme.MediumSeaGreen
import com.example.solariotmobile.ui.theme.PaleGreen
import com.example.solariotmobile.viewmodel.CommandViewModel
import java.time.format.DateTimeFormatter

@Composable
fun CommandScreen(viewModel: CommandViewModel = hiltViewModel()) {

    val lastResistanceState by viewModel.lastResistanceState.collectAsState()
    val failureResistanceState by viewModel.isFailureResistanceState.collectAsState()
    val messageResistanceState by viewModel.getMessageResistanceState.collectAsState()
    val loadingResistanceState by viewModel.isLoadingResistanceState.collectAsState()
    val isResistanceActive by viewModel.isResistanceActive.collectAsState()

    val failureEspParameter by viewModel.isFailureEspParameter.collectAsState()
    val messageEspParameter by viewModel.getMessageEspParameter.collectAsState()
    val loadingEspParameter by viewModel.isLoadingEspParameter.collectAsState()
    val espIp by viewModel.espIp.collectAsState()
    val espPort by viewModel.espPort.collectAsState()
    val espProtocol by viewModel.espProtocol.collectAsState()

    var newEspIp by remember { mutableStateOf("") }
    var newEspPort by remember { mutableStateOf("") }
    var newEspProtocol by remember { mutableStateOf("") }

    val switchEnable =
        !loadingResistanceState && espIp.isNotEmpty() && espPort.isNotEmpty() && espProtocol.isNotEmpty()

    LaunchedEffect(Unit) {
        viewModel.fetchLastResistanceState()
        viewModel.fetchEspParameters()
    }

    LaunchedEffect(espIp, espPort, espProtocol) {
        newEspIp = espIp
        newEspPort = espPort
        newEspProtocol = espProtocol
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
            enabled = switchEnable,
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

        if (loadingResistanceState) {
            LoadingComponent()
        }

        if (failureResistanceState) {
            FailureComponent(messageResistanceState)
        }

        if (loadingEspParameter) {
            LoadingComponent()
        }

        if (messageEspParameter.isNotEmpty()) {
            val backgroundColor: Color = if (failureEspParameter) {
                Color.Red
            } else {
                Color.Green
            }
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                ) {
                    Text(messageEspParameter, color = Color.Black)
                    IconButton(
                        onClick = { viewModel.deleteMessageEspParameter() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = Color.Black,
                            contentDescription = "Delete icon",
                        )
                    }
                }
            }
        }

        FlowRow(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = newEspIp,
                onValueChange = { value ->
                    newEspIp = value
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FirstGreenForGradient
                ),
                label = { Text("Adresse IP", color = Color.Black) },
            )
            OutlinedTextField(
                value = newEspPort,
                onValueChange = { value ->
                    newEspPort = value
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FirstGreenForGradient
                ),
                label = { Text("Port", color = Color.Black) }
            )
            OutlinedTextField(
                value = newEspProtocol,
                onValueChange = { value ->
                    newEspProtocol = value
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FirstGreenForGradient
                ),
                label = { Text("Protocole", color = Color.Black) }
            )
        }
        ButtonWithIconAndText(
            onClickButton = { viewModel.saveEspParameters(newEspIp, newEspPort, newEspProtocol) },
            text = "Enregistrer",
            icon = Icons.Default.Save,
            iconContentDescription = "Enregistrer"
        )
    }
}
