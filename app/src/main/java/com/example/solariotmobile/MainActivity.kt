package com.example.solariotmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.solariotmobile.ui.command.CommandScreen
import com.example.solariotmobile.ui.grid.GridScreen
import com.example.solariotmobile.ui.settings.SettingsScreen
import com.example.solariotmobile.ui.temperatures.TemperaturesScreen
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

sealed class NavDestination(val title: String, val route: String, val icon: ImageVector) {
    object Temperatures: NavDestination(title = "Temperatures", route = "temperatures", icon = Icons.Filled.Home)
    object Grid: NavDestination(title = "Table", route = "grid", icon = Icons.Filled.MoreVert)
    object Command: NavDestination(title = "Commande", route = "command", icon = Icons.Filled.Build)
    object Settings: NavDestination(title = "Paramètres", route = "settings", icon = Icons.Filled.Settings)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val items = listOf(
                NavDestination.Temperatures,
                // TODO réactiver la route quand le dev sera terminé
                // NavDestination.Grid,
                NavDestination.Command
            )
            var selectedIndex by remember { mutableIntStateOf(0) }

            Scaffold(
                topBar = {},
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White
                    ) {
                        items.forEachIndexed { index, screen ->
                            NavigationBarItem(
                                icon = {
                                    Icon(imageVector = screen.icon, contentDescription = null)
                                },
                                label = { Text(screen.title) },
                                selected = index == selectedIndex,
                                onClick = {
                                    selectedIndex = index
                                    // when clicking on settings, the route stay inside the navController and generate an error when changing page
                                    // Then we need to delete the route settings inside navController
                                    if (navController.currentDestination != null && navController.currentDestination!!.route == NavDestination.Settings.route) {
                                        navController.popBackStack()
                                    }
                                    navController.navigate(screen.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = FirstGreenForGradient,
                                    selectedTextColor = FirstGreenForGradient,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(Modifier.padding(innerPadding)) {
                    Column {
                        IconButton(modifier = Modifier.align(Alignment.End), onClick = {
                            navController.navigate(NavDestination.Settings.route)
                        }) {
                            Icon(
                                NavDestination.Settings.icon,
                                contentDescription = NavDestination.Settings.title
                            )
                        }
                        NavigationHost(
                            navController = navController,
                            innerPadding = innerPadding
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun NavigationHost(
        viewModel: MainActivityViewModel = hiltViewModel(),
        navController: NavHostController,
        innerPadding: PaddingValues
    ) {

        val isConnexionInformationValid = viewModel.isValidConnexionInformation()

        NavHost(
            navController = navController,
            startDestination = if (!isConnexionInformationValid) NavDestination.Settings.route else NavDestination.Temperatures.route,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
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
            composable(route = NavDestination.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
