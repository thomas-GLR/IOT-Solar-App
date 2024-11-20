package com.example.solariotmobile.ui.command

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CommandScreen() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Command")
        IconButton(onClick = { }) {
            Icon(Icons.Filled.CheckCircle, contentDescription = "Start")
        }
    }
}
