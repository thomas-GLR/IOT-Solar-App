package com.example.solariotmobile.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.data.TemperatureDto
import java.time.format.DateTimeFormatter

@Composable
fun TableComponent(
    temperatures: List<TemperatureDto>,
    tablePadding: Dp = 0.dp
) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(tablePadding)
        ) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray
                    ),
            ) {
                Text(
                    text = "Température",
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .height(38.dp)
                        .wrapContentHeight()
                        .padding(end = 8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(2f)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray
                    ),
            ) {
                Text(
                    text = "Date",
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .height(38.dp)
                        .wrapContentHeight()
                        .padding(end = 8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(2f)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray
                    ),
            ) {
                Text(
                    text = "Sonde",
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .height(38.dp)
                        .wrapContentHeight()
                        .padding(end = 8.dp)
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(temperatures) { temperature ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(tablePadding)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .border(
                                width = 1.dp,
                                color = Color.LightGray
                            ),
                    ) {
                        Text(
                            text = "${temperature.temperature}",
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .height(38.dp)
                                .wrapContentHeight()
                                .padding(end = 8.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .border(
                                width = 1.dp,
                                color = Color.LightGray
                            ),
                    ) {
                        Text(
                            text = temperature.collectionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")),
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .height(38.dp)
                                .wrapContentHeight()
                                .padding(end = 8.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .border(
                                width = 1.dp,
                                color = Color.LightGray
                            ),
                    ) {
                        Text(
                            text = temperature.readingDeviceName.value,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .height(38.dp)
                                .wrapContentHeight()
                                .padding(end = 8.dp)
                        )
                    }
                }
            }
        }
    }
}