package com.example.solariotmobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.solariotmobile.ui.screens.CommandScreen
import com.example.solariotmobile.ui.screens.GridScreen
import com.example.solariotmobile.ui.screens.TemperaturesScreen

sealed class NavDestination(val title: String, val route: String, val icon: ImageVector) {
    data object Temperatures: NavDestination(title = "Temperatures", route = "temperatures", icon = Icons.Filled.Thermostat)
    data object Grid: NavDestination(title = "Données", route = "grid", icon = Icons.Filled.GridOn)
    data object Command: NavDestination(title = "Résistance", route = "command", icon = Icons.Filled.SettingsRemote)
}

@Composable
fun MainNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Temperatures.route
    ) {
        composable(route = NavDestination.Temperatures.route) {
            TemperaturesScreen()
        }
        composable(route = NavDestination.Grid.route) {
            GridScreen()
        }
        composable(route = NavDestination.Command.route) {
            CommandScreen()
        }
    }
}
