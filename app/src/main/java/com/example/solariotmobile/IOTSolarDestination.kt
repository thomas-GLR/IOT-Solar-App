package com.example.solariotmobile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.solariotmobile.ui.command.CommandScreen
import com.example.solariotmobile.ui.grid.GridScreen
import com.example.solariotmobile.ui.temperatures.TemperaturesScreen

interface IOTSolarDestination {
    val icon: ImageVector
    val route:String
    val screen:@Composable () -> Unit
}

/**
 * Rally app navigation destinations
 */
object Temperatures : IOTSolarDestination {
    override val icon = Icons.Filled.Home
    override val route = "temperatures"
    override val screen: @Composable () -> Unit = { TemperaturesScreen() }
}

object Grid : IOTSolarDestination {
    override val icon = Icons.Filled.List
    override val route = "grid"
    override val screen: @Composable () -> Unit = { GridScreen() }
}

object Command : IOTSolarDestination {
    override val icon = Icons.Filled.Build
    override val route = "command"
    override val screen: @Composable () -> Unit = { CommandScreen() }
}

// Screens to be displayed in the top RallyTabRow
val rallyTabRowScreens = listOf(Temperatures, Grid, Command)