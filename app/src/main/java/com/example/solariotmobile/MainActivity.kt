package com.example.solariotmobile

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.ui.theme.SolarIOTMobileTheme

// Color.White
// Brush.horizontalGradient(listOf(Color(0xFF32CD32), Color(0xFF82CF00)))
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SolarIOTMobileTheme {
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = topAppBarColors(
                                containerColor = Color.Green,
                                titleContentColor = Color.White,
                            ),
                            title = {
                                Text(text = "IOT Solar App")
                            },
                            navigationIcon = {
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Arrow back"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { /* do something */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Settings"
                                    )
                                }
                            },
                            scrollBehavior = scrollBehavior,
                        )
                    },
                    bottomBar = {
                        BottomAppBar(
                            containerColor = Color.Green,
                            contentColor = Color.White,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = {}) {
                                    Icon(
                                        Icons.Outlined.Home,
                                        contentDescription = "Bouton accueil"
                                    )
                                }
                                IconButton(onClick = {}) {
                                    Icon(
                                        Icons.Outlined.List,
                                        contentDescription = "Bouton liste température"
                                    )
                                }
                                IconButton(onClick = {}) {
                                    Icon(
                                        Icons.Outlined.Build,
                                        contentDescription = "Bouton activation chauffe eau"
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    val configuration = LocalConfiguration.current
                    Box(Modifier.padding(innerPadding)) {
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                LandscapeDisplay()
                            }

                            else -> {
                                PortraitDisplay()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LandscapeDisplay(modifier: Modifier = Modifier) {
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
            // Creating a Canvas to draw a Circle
            TemperatureDisplay(
                temperature = 50.0,
                "HAUT"
            )

            Spacer(modifier = Modifier.width(200.dp))

            // Creating a Canvas to draw a Circle
            TemperatureDisplay(
                temperature = 45.0,
                "MILIEU"
            )

            Spacer(modifier = Modifier.width(200.dp))

            // Creating a Canvas to draw a Circle
            TemperatureDisplay(
                temperature = 80.0,
                "BAS"
            )
        }
    }
}

@Composable
fun PortraitDisplay(modifier: Modifier = Modifier) {
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
            TemperatureDisplay(
                temperature = 50.0,
                "HAUT"
            )

            Spacer(modifier = Modifier.height(150.dp))

            // Creating a Canvas to draw a Circle
            TemperatureDisplay(
                temperature = 45.0,
                "MILIEU"
            )

            Spacer(modifier = Modifier.height(150.dp))

            // Creating a Canvas to draw a Circle
            TemperatureDisplay(
                temperature = 80.0,
                "BAS"
            )
        }
    }
}

@Composable
fun TemperatureDisplay(
    temperature: Double,
    readingDeviceName: String,
    modifier: Modifier = Modifier
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

//@Preview
//@Composable
//fun TemperatureDisplayPreview(
//    @PreviewParameter(TemperaturePreviewParameterProvider::class) temperature: Double
//) {
//    TemperatureDisplay(temperature)
//}

class TemperaturePreviewParameterProvider : PreviewParameterProvider<Double> {
    override val values = sequenceOf(
        45.0
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SolarIOTMobileTheme {
        Greeting("Android")
    }
}