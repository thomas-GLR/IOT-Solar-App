package com.example.solariotmobile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.solariotmobile.ui.command.CommandScreen
import com.example.solariotmobile.ui.grid.GridScreen
import com.example.solariotmobile.ui.settings.SettingsScreen
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
    override val icon = Icons.Filled.MoreVert
    override val route = "grid"
    override val screen: @Composable () -> Unit = { GridScreen() }
}

object Command : IOTSolarDestination {
    override val icon = Icons.Filled.Build
    override val route = "command"
    override val screen: @Composable () -> Unit = { CommandScreen() }
}

object Settings : IOTSolarDestination {
    override val icon = Icons.Filled.Settings
    override val route = "settings"
    override val screen: @Composable () -> Unit = { ( SettingsScreen() ) }
}

// Screens to be displayed in the top RallyTabRow
val rallyTabRowScreens = listOf(Temperatures, Grid, Command)