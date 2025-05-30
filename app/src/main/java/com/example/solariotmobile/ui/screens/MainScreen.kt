package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.yml.charts.common.extensions.isNotNull
import com.example.solariotmobile.ui.navigation.MainNavigation
import com.example.solariotmobile.ui.navigation.MainNavDestination
import com.example.solariotmobile.ui.theme.FirstGreenForGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        MainNavDestination.Temperatures,
        MainNavDestination.Grid,
        MainNavDestination.Command
    )
    var selectedIndex by remember { mutableIntStateOf(0) }
    var showBackButton by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    LaunchedEffect(currentRoute) {
        showBackButton =
            currentRoute.isNotNull() && currentRoute == MainNavDestination.GridDetail.route
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Se déconnecter"
                        )
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
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
                            if (index != selectedIndex) {
                                selectedIndex = index
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
            MainNavigation(navController)
        }
    }
}