package com.example.solariotmobile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solariotmobile.data.CreateResistanceStateDto
import com.example.solariotmobile.data.ResistanceStateDto
import com.example.solariotmobile.repository.CommandRepository
import com.example.solariotmobile.repository.SettingRepository
import com.example.solariotmobile.utils.ErrorResponseFactory
import com.example.solariotmobile.utils.LocalDateTimeAdapter
import com.example.solariotmobile.utils.NetworkUtils
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class CommandUiState {
    data object Loading : CommandUiState()
    data class Success(
        val lastResistanceState: ResistanceStateDto,
        val isResistanceActive: Boolean,
        val isWaitingForSseResponse: Boolean = false
    ) : CommandUiState()
    data class Error(val message: String) : CommandUiState()
}

@HiltViewModel
class CommandViewModel @Inject constructor(
    private val repository: CommandRepository,
    private val settingRepository: SettingRepository,
    private val okHttpClient: OkHttpClient
) :
    ViewModel() {
    val TAG = "CommandViewModel"

    private var eventSource: EventSource? = null
    private var isSseActive = false
    private var reconnectJob: Job? = null

    private val _uiState = MutableStateFlow<CommandUiState>(
        CommandUiState.Success(
            lastResistanceState = ResistanceStateDto(
                null,
                now(),
                currentState = false,
                requestedState = false
            ),
            isResistanceActive = false,
            isWaitingForSseResponse = false
        )
    )
    val uiState: StateFlow<CommandUiState> = _uiState.asStateFlow()

    fun startSse() {
        if (!isSseActive) {
            isSseActive = true
            viewModelScope.launch {
                connectSse()
            }
        }
    }

    fun stopSse() {
        isSseActive = false
        reconnectJob?.cancel()
        reconnectJob = null
        eventSource?.cancel()
        eventSource = null
        Log.i(TAG, "SSE stream stopped")
    }

    private suspend fun connectSse() {
        eventSource?.cancel()
        val serverAddressFromSettings = settingRepository.getServerAddress.first()
        val serverPortFromSettings = settingRepository.getServerPort.first()
        val networkProtocolFromSettings = settingRepository.getNetworkProtocol.first()

        val serverAddress = serverAddressFromSettings.takeIf { it.isNotEmpty() } ?: "localhost"
        val serverPort = serverPortFromSettings.takeIf { it.isNotEmpty() } ?: "8080"
        val networkProtocol = networkProtocolFromSettings.takeIf { it.isNotEmpty() } ?: "http"

        if (!isSseActive) return

        val baseUrl = "${networkProtocol}://${NetworkUtils.getServerUrl(serverAddress, serverPort)}"

        val client = okHttpClient.newBuilder()
            .readTimeout(0, TimeUnit.MILLISECONDS) // pas de timeout pour SSE
            .build()

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        val request = Request.Builder()
            .url("$baseUrl/resistance/ack/stream")
            .addHeader("Accept", "text/event-stream")
            .build()

        val factory = EventSources.createFactory(client)

        eventSource = factory.newEventSource(request, object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.i(TAG ,"Connexion au serveur SSE")
                // Connecté
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                try {
                    val ackEvent = gson.fromJson(data, ResistanceStateDto::class.java)
                    val currentState = _uiState.value
                    if (currentState is CommandUiState.Success) {
                        _uiState.value = currentState.copy(
                            lastResistanceState = ackEvent,
                            isResistanceActive = ackEvent.currentState ?: false,
                            isWaitingForSseResponse = false
                        )
                    }
                    Log.d(TAG, "Evénement reçu: $ackEvent")
                } catch (e: Exception) {
                    Log.e(TAG, "Erreur parsing JSON", e)
                }
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?
            ) {
                Log.e(TAG, "Echec SSE: ${t?.message}. Tentative de reconnexion dans 5s...")

                if (response != null) {
                    Log.e(TAG, "[${response.code}] ${response.message}")
                }

                // Important : on ferme la source actuelle avant de relancer
                eventSource.cancel()
                
                // Fetch state to keep UI updated while disconnected
                fetchLastResistanceState()

                // Lancer la reconnexion après un délai
                reconnectJob = viewModelScope.launch {
                    delay(5000) // Attendre 5 secondes avant de réessayer
                    if (isSseActive) connectSse()
                }
            }

            override fun onClosed(eventSource: EventSource) {
                Log.i(TAG, "SSE fermé. Reconnexion...")
                
                // Fetch state to keep UI updated
                fetchLastResistanceState()
                
                reconnectJob = viewModelScope.launch {
                    delay(2000)
                    if (isSseActive) connectSse()
                }
            }
        })
    }

    fun fetchLastResistanceState() {
        Log.i(TAG, "Fetching last resistance state...")
        viewModelScope.launch {
            _uiState.value = CommandUiState.Loading
            try {
                val response = repository.getLastResistanceState()

                if (response.isSuccessful && response.body() != null) {
                    val resistanceState = response.body()!!
                    _uiState.value = CommandUiState.Success(
                        lastResistanceState = resistanceState,
                        isResistanceActive = resistanceState.currentState ?: resistanceState.requestedState,
                        isWaitingForSseResponse = resistanceState.currentState == null
                    )
                } else {
                    _uiState.value = CommandUiState.Error(
                        ErrorResponseFactory.createErrorMessage(
                            response.code(),
                            response.errorBody()
                        )
                    )
                }
            } catch (exception: Exception) {
                _uiState.value = CommandUiState.Error(
                    exception.message ?: "Une exception est survenue"
                )
            }
        }
    }

    fun createResistanceState(currentState: Boolean) {
        viewModelScope.launch {
            val currentUiState = _uiState.value
            if (currentUiState is CommandUiState.Success) {
                // Mettre en attente immédiatement
                _uiState.value = currentUiState.copy(isWaitingForSseResponse = true)
                
                try {
                    val response = repository.createResistanceState(CreateResistanceStateDto(currentState))

                    if (response.isSuccessful && response.body() != null) {
                        val resistanceStateResponse = response.body()!!
                        _uiState.value = currentUiState.copy(
                            lastResistanceState = currentUiState.lastResistanceState.copy(
                                id = resistanceStateResponse.id,
                                lastUpdate = resistanceStateResponse.lastUpdate,
                                currentState = resistanceStateResponse.currentState,
                                requestedState = resistanceStateResponse.requestedState
                            ),
                            isResistanceActive = resistanceStateResponse.requestedState,
                            isWaitingForSseResponse = true // Rester en attente pour SSE
                        )
                    } else {
                        _uiState.value = currentUiState.copy(
                            isWaitingForSseResponse = false
                        )
                        val errorMessage = if (response.errorBody() != null) {
                            response.errorBody()!!.string()
                        } else {
                            "Une erreur est survenue"
                        }
                        _uiState.value = CommandUiState.Error(errorMessage)
                    }
                } catch (exception: Exception) {
                    _uiState.value = CommandUiState.Error(
                        exception.message ?: "Une exception est survenue"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        stopSse()
        super.onCleared()
    }

}