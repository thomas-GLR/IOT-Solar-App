package com.example.solariotmobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.ui.screens.CommandScreen
import com.example.solariotmobile.ui.screens.DetailGridScreen
import com.example.solariotmobile.ui.screens.GridScreen
import com.example.solariotmobile.ui.screens.temperatureScreen.TemperaturesScreen
import java.time.LocalDate

sealed class MainNavDestination(val title: String, val route: String, val icon: ImageVector) {
    data object Temperatures : MainNavDestination(
        title = "Températures",
        route = "temperatures",
        icon = Icons.Filled.Thermostat
    )

    data object Grid :
        MainNavDestination(title = "Données", route = "grid", icon = Icons.Filled.GridOn)

    data object Command : MainNavDestination(
        title = "Résistance",
        route = "command",
        icon = Icons.Filled.SettingsRemote
    )

    data object GridDetail : MainNavDestination(
        title = "Détail",
        route = "gridDetail/{date}/{deviceName}",
        icon = Icons.Filled.GridOn
    )
}

@Composable
fun MainNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.Temperatures.route
    ) {
        composable(route = MainNavDestination.Temperatures.route) {
            TemperaturesScreen()
        }
        composable(route = MainNavDestination.Grid.route) {
            GridScreen(navController)
        }
        composable(route = MainNavDestination.Command.route) {
            CommandScreen()
        }
        composable(
            route = MainNavDestination.GridDetail.route,
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("deviceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val date = LocalDate.parse(backStackEntry.arguments?.getString("date"))
            val deviceName = ReadingDeviceName.valueOf(
                backStackEntry.arguments?.getString("deviceName") ?: "TOP"
            )
            DetailGridScreen(date = date, deviceName = deviceName)
        }
    }
}
