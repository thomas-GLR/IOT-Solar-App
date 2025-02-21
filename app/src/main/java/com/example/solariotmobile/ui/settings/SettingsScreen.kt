package com.example.solariotmobile.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.solariotmobile.ui.components.LoadingComponent
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import com.example.solariotmobile.ui.theme.ForestGreen
import com.example.solariotmobile.ui.theme.MediumSeaGreen
import com.example.solariotmobile.ui.theme.PrincipalGradient

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val serverAddress by viewModel.serverAddressState.collectAsState()
    val serverPort by viewModel.serverPortState.collectAsState()

    var loading by remember { mutableStateOf(false) }
    var failure by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }

    viewModel.isLoading.observe(lifecycleOwner) { isLoading ->
        loading = isLoading
    }
    viewModel.isFailure.observe(lifecycleOwner) { isFailure ->
        failure = isFailure
    }
    viewModel.getMessage.observe(lifecycleOwner) { getMessage ->
        message = getMessage
    }

    // When the screen is initialized the first time, textfield are blank.
    // LaunchedEffect fill them when the 2 arguments changes
    LaunchedEffect(serverAddress, serverPort) {
        address = serverAddress
        port = serverPort
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = address,
            onValueChange = {
                address = it
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FirstGreenForGradient
            ),
            label = { Text("Adresse du serveur") }
        )
        OutlinedTextField(
            value = port,
            onValueChange = {
                port = it
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FirstGreenForGradient
            ),
            label = { Text("Port") }
        )
        Button(
            onClick = { viewModel.saveServerSettings(address, port) },
            colors = ButtonDefaults.buttonColors(
                containerColor = FirstGreenForGradient
            )
        ) {
            Text("Enregistrer")
        }
        Button(
            onClick = { viewModel.fetchData(address, port) },
            colors = ButtonDefaults.buttonColors(
                containerColor = FirstGreenForGradient
            )
        ) {
            Text("Tester la connexion")
        }
        if (loading) {
            LoadingComponent()
        }
        if (message.isNotEmpty()) {
            val backgroundColor: Color = if (failure) {
                Color.Red
            } else {
                Color.Green
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .padding(16.dp)
            ) {
                Text(message)
            }
        }
    }
}