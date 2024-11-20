package com.example.solariotmobile.ui.temperatures

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.api.ApiResponse
import com.example.solariotmobile.api.TemperatureWebService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun TemperaturesScreen() {
    var loading by remember { mutableStateOf(true) }
    var failed by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var lastTemperatures = mutableListOf<TemperatureDto>()

    val configuration = LocalConfiguration.current

    val retrofit = Retrofit.Builder()
        .baseUrl("http://147.79.100.54:3000/temperatures/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val temperaturesWebService: TemperatureWebService =
        retrofit.create(TemperatureWebService::class.java)

    val callGetLastTemperatures = temperaturesWebService.getLastTemperatures()

    callGetLastTemperatures.enqueue(object : Callback<List<TemperatureDto>> {
        override fun onResponse(
            call: Call<List<TemperatureDto>>,
            response: Response<List<TemperatureDto>>
        ) {
            loading = true
            failed = false
            lastTemperatures = response.body() as MutableList<TemperatureDto>
            var apiResponse: ApiResponse<List<TemperatureDto>>
            apiResponse.failure = true
        }

        override fun onFailure(call: Call<List<TemperatureDto>>, throwable: Throwable) {
            failed = true
            loading = false
            message = throwable.message ?: ""
        }

    })

    if (loading) {
        Box(modifier = Modifier.fillMaxWidth()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }

    if (failed) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(Color.Red)
                    .padding(16.dp)
            ) {
                Text("Un problème est survenue : $message")
            }
            Button(onClick = { }) {
                Text("Recharger la page")
            }
        }
    } else {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                LandscapeDisplay(lastTemperatures = lastTemperatures)
            }

            else -> {
                PortraitDisplay(lastTemperatures = lastTemperatures)
            }
        }
    }
}

fun loadLastTemperatures() {

}

@Composable
fun LandscapeDisplay(
    lastTemperatures: List<TemperatureDto>
) {
    Column(
        Modifier
            .fillMaxWidth(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterVertically
        ) {
            lastTemperatures.forEach { temperature ->
                // Creating a Canvas to draw a Circle
                TemperatureDisplay(
                    temperature = temperature.temperature,
                    temperature.readingDeviceName.name
                )

                Spacer(modifier = Modifier.width(200.dp))
            }
        }
    }
}

@Composable
fun PortraitDisplay(
    lastTemperatures: List<TemperatureDto>
) {
    Row(
        Modifier.fillMaxSize(),
        Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .fillMaxWidth(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            lastTemperatures.forEach { temperature ->
                TemperatureDisplay(
                    temperature = temperature.temperature,
                    temperature.readingDeviceName.name
                )
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}

@Composable
fun TemperatureDisplay(
    temperature: Double,
    readingDeviceName: String
) {
    Column(
        Modifier
            .fillMaxWidth(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Text(
            text = "$temperature°C",
            modifier = Modifier
                .drawWithCache {
                    val brush =
                        Brush.horizontalGradient(listOf(Color(0xFF32CD32), Color(0xFF82CF00)))
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    onDrawBehind {

                        drawCircle(
                            brush = brush,
                            // center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                            radius = 400F / 2,
                            style = Stroke(80F)
                        )
                    }
                }
        )
        Text(text = readingDeviceName)
    }
}