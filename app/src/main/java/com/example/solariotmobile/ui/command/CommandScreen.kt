package com.example.solariotmobile.ui.command

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable

@Composable
fun CommandScreen() {
    Text(text = "Command")
    IconButton(onClick = {  }) {
        Icon(Icons.Filled.CheckCircle, contentDescription = "Start")
    }
}
