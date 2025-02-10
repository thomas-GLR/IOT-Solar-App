package com.example.solariotmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.solariotmobile.ui.components.IOTSolarTabRow
import com.example.solariotmobile.ui.theme.SolarIOTMobileTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SolarIOTMobileApp()
        }
    }
}

@Composable
fun SolarIOTMobileApp() {
    SolarIOTMobileTheme {
        var currentScreen: IOTSolarDestination by remember { mutableStateOf(Temperatures) }
        Scaffold(
            topBar = {},
            bottomBar = {
                IOTSolarTabRow(
                    allScreens = rallyTabRowScreens,
                    onTabSelected = { screen -> currentScreen = screen },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                Column {
                    IconButton(modifier = Modifier.align(Alignment.End), onClick = {
                            currentScreen = Settings
                        }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Paramètre")
                    }
                    currentScreen.screen()
                }
            }
        }
    }
}
