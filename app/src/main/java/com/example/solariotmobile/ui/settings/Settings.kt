package com.example.solariotmobile.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen() {
    var address by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Adresse du serveur") }
        )
        OutlinedTextField(
            value = port,
            onValueChange = { port = it },
            label = { Text("Port") }
        )
        Button(onClick = {  }) {
            Text("Tester la connexion")
        }
    }
}