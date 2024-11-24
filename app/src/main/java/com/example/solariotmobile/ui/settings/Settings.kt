package com.example.solariotmobile.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.solariotmobile.api.TemperatureWebService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {

    val serverAddress by viewModel.serverAddressState.collectAsState()
    val serverPort by viewModel.serverPortState.collectAsState()

    var loading by remember { mutableStateOf(false) }
    var failure by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }

    // When the screen is initialized the first time, textfield are blank.
    // LaunchedEffect fill them when the 2 arguments changes
    LaunchedEffect(serverAddress, serverPort) {
        address = serverAddress
        port = serverPort
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = address,
            onValueChange = {
                address = it
        },
            label = { Text("Adresse du serveur") }
        )
        OutlinedTextField(
            value = port,
            onValueChange = {
                port = it
            },
            label = { Text("Port") }
        )
        Button(onClick = {
            viewModel.saveServerSettings(address, port)
        }) {
            Text("Enregistrer")
        }
        Button(onClick = {
            message = ""
            loading = true
            failure = false

            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.105:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val temperaturesWebService: TemperatureWebService =
                retrofit.create(TemperatureWebService::class.java)

            val callGetLastTemperatures = temperaturesWebService.getHelloWorld()

            callGetLastTemperatures.enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    loading = false
                    failure = false
                    message = "Connexion réussie !"
                }

                override fun onFailure(call: Call<String>, throwable: Throwable) {
                    failure = true
                    loading = false
                    message = throwable.message ?: "Aucun message d'erreur"
                }

            })
        }) {
            Text("Tester la connexion")
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