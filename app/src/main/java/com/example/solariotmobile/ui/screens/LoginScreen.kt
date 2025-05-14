package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.solariotmobile.ui.components.LoadingComponent
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import com.example.solariotmobile.viewmodel.LoginState
import com.example.solariotmobile.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    var stayConnected by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connexion") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Paramètres"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.DevicesOther,
                    contentDescription = "Logo",
                    modifier = Modifier.size(300.dp),
                    tint = FirstGreenForGradient
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FirstGreenForGradient
                    ),
                    label = { Text("Nom d'utilisateur") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FirstGreenForGradient
                    ),
                    label = { Text("Mot de passe") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                if (loginState is LoginState.Error) {
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = stayConnected,
                        onCheckedChange = { stayConnected = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = FirstGreenForGradient
                        )
                    )
                    Text(text = "Rester connecté")
                }

                Button(
                    onClick = { viewModel.login(username, password, stayConnected) },
                    enabled = username.isNotEmpty() && password.isNotEmpty() && loginState !is LoginState.Loading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FirstGreenForGradient
                    )
                ) {
                    if (loginState is LoginState.Loading) {
                        LoadingComponent(35.dp)
                    } else {
                        Text("Se connecter")
                    }
                }
            }
        }
    }

//    Column(
//        Modifier.fillMaxSize(),
//        Arrangement.Center,
//        Alignment.CenterHorizontally
//    ) {
//        Text("Connexion")
//        OutlinedTextField(
//            value = username,
//            onValueChange = {
//                username = it
//            },
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = FirstGreenForGradient
//            ),
//            label = { Text("Nom d'utilisateur") }
//        )
//        OutlinedTextField(
//            value = password,
//            onValueChange = {
//                password = it
//            },
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = FirstGreenForGradient
//            ),
//            label = { Text("Mot de passe") },
//            visualTransformation = PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//        )
//        Button(
//            onClick = { viewModel.login(username, password, viewModel.getNetworkProtocol(isHttpsEnabled)) },
//            colors = ButtonDefaults.buttonColors(
//                containerColor = FirstGreenForGradient
//            )
//        ) {
//            Text("Se connecter")
//        }
//    }
}