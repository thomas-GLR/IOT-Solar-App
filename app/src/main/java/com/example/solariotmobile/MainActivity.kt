package com.example.solariotmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.solariotmobile.ui.components.IOTSolarTabRow
import com.example.solariotmobile.ui.theme.SolarIOTMobileTheme

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
            bottomBar ={
                IOTSolarTabRow(
                    allScreens = rallyTabRowScreens,
                    onTabSelected = { screen -> currentScreen = screen },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                currentScreen.screen()
            }
        }
    }
}
