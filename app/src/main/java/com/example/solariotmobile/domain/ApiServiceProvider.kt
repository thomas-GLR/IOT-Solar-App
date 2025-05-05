package com.example.solariotmobile.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Class that manages the reloading of API services when parameters are modified
 */
@Singleton
class ApiServiceProvider @Inject constructor(
    private val authServiceProvider: Provider<AuthService>,
    private val temperatureWebServiceProvider: Provider<TemperatureWebService>
) {
    private val _serviceReloadTrigger = MutableStateFlow(0)
    val serviceReloadTrigger: StateFlow<Int> = _serviceReloadTrigger.asStateFlow()

    val authService: AuthService
        get() = authServiceProvider.get()

    val temperatureWebService: TemperatureWebService
        get() = temperatureWebServiceProvider.get()

    /**
     * Reload all services
     */
    fun triggerServiceReload() {
        _serviceReloadTrigger.value += 1
        println("ApiServiceProvider : rechargement des services déclenché : ${_serviceReloadTrigger.value}")
    }
}