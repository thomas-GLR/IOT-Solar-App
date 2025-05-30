package com.example.solariotmobile.ui.components.tableComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.ui.components.LoadingComponent
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import com.example.solariotmobile.utils.NumberUtils.numberToFranceFormat
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun TableComponent(
    temperatures: List<TemperatureDto>,
    isLoading: Boolean,
    headers: List<String>,
    contentRow: @Composable ((temperature: TemperatureDto) -> Unit)
) {
    Column(
        Modifier
            .fillMaxSize()
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HeadersComponent(
            headers,
            Modifier.align(Alignment.Start)
        )

        val listState = rememberLazyListState()

        if (isLoading) {
            Spacer(modifier = Modifier.weight(1f))
            LoadingComponent()
            Spacer(modifier = Modifier.weight(1f))
        } else {
            LazyColumnScrollbar(
                state = listState,
                settings = ScrollbarSettings(
                    alwaysShowScrollbar = true,
                    thumbSelectedColor = FirstGreenForGradient,
                    thumbUnselectedColor = FirstGreenForGradient
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            ) {
                LazyColumn(
                    state = listState
                ) {
                    items(temperatures) { temperature ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            contentRow(temperature)
                            HorizontalDivider(
                                color = Color.LightGray,
                                modifier = Modifier.fillMaxWidth(0.9f),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "${numberToFranceFormat(temperatures.size)} résultats",
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.End)
        )
    }
}