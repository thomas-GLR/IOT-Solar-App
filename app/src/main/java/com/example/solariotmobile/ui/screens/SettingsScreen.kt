package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.solariotmobile.ui.components.LoadingComponent
import com.example.solariotmobile.viewmodel.SettingsViewModel
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import com.example.solariotmobile.ui.theme.ForestGreen
import com.example.solariotmobile.ui.theme.LightGreen
import com.example.solariotmobile.ui.theme.MediumSeaGreen
import com.example.solariotmobile.ui.theme.PaleGreen

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val serverAddress by viewModel.serverAddressState.collectAsState()
    val serverPort by viewModel.serverPortState.collectAsState()
    val serverUsername by viewModel.serverUsernameState.collectAsState()
    val serverPassword by viewModel.serverPasswordState.collectAsState()
    val networkProtocol by viewModel.networkProtocolState.collectAsState()

    var loading by remember { mutableStateOf(false) }
    var failure by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isHttpsEnabled by remember { mutableStateOf(false) }

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
        username = serverUsername
        password = serverPassword
        isHttpsEnabled = networkProtocol
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
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
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FirstGreenForGradient
            ),
            label = { Text("Nom d'utilisateur") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FirstGreenForGradient
            ),
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .clickable { isHttpsEnabled = !isHttpsEnabled }
        ) {
            Text(text = "Utiliser HTTPS (securisé)")
            Spacer(modifier = Modifier.height(8.dp))
            Switch(
                checked = isHttpsEnabled,
                onCheckedChange = {
                    isHttpsEnabled = it
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ForestGreen,
                    checkedTrackColor = LightGreen,
                    uncheckedThumbColor = MediumSeaGreen,
                    uncheckedTrackColor = PaleGreen
                ),
                modifier = Modifier.scale(1.5f)
            )
        }
        Button(
            onClick = { viewModel.saveServerSettings(address, port, username, password, viewModel.getNetworkProtocol(isHttpsEnabled)) },
            colors = ButtonDefaults.buttonColors(
                containerColor = FirstGreenForGradient
            )
        ) {
            Text("Enregistrer")
        }
        Button(
            onClick = { viewModel.fetchData(address, port, username, password, viewModel.getNetworkProtocol(isHttpsEnabled)) },
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