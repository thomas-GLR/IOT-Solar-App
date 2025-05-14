package com.example.solariotmobile.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.solariotmobile.viewmodel.LoginViewModel
import com.example.solariotmobile.viewmodel.SplashScreenViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun SplashScreen(
    splashViewModel: SplashScreenViewModel = hiltViewModel(),
    onNoCredentials: () -> Unit,
    onLoginSuccess: () -> Unit
) {

    LaunchedEffect(Unit) {
        val usernameAndPassword = splashViewModel.getCredential()
        if (usernameAndPassword.first.isNullOrEmpty() || usernameAndPassword.second.isNullOrEmpty()) {
            onNoCredentials()
            return@LaunchedEffect
        }

        val username: String = usernameAndPassword.first!!
        val password: String = usernameAndPassword.second!!

        val result: Boolean = splashViewModel.login(username, password)

        if (result) {
            onLoginSuccess()
            return@LaunchedEffect
        }

        onNoCredentials()
    }

    Text("Splash")
}