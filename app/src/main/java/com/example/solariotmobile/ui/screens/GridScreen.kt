package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.ui.components.FailureWithRefresh
import com.example.solariotmobile.ui.components.tableComponent.ButtonCellComponent
import com.example.solariotmobile.ui.components.tableComponent.TableComponent
import com.example.solariotmobile.ui.components.tableComponent.TextCellComponent
import com.example.solariotmobile.ui.navigation.MainNavDestination
import com.example.solariotmobile.viewmodel.TemperaturesViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun GridScreen(navController: NavController, viewModel: TemperaturesViewModel = hiltViewModel()) {
    val loading by viewModel.isLoading.collectAsState()
    val failure by viewModel.isFailure.collectAsState()
    val message by viewModel.getMessage.collectAsState()
    val temperatures by viewModel.getTemperaturesFiltered.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    if (failure) {
        FailureWithRefresh(message) { viewModel.fetchData() }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(25.dp)
        ) {
            FilterMenu(viewModel)

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
                    .background(Color(204, 229, 255), RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Information"
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text("Les températures affichés sont la moyenne de toutes les températures prélevées pour la date affichée")
            }

            Spacer(modifier = Modifier.height(20.dp))

            TableComponent(
                temperatures = temperatures,
                isLoading = loading,
                headers = listOf("Température", "Date", "Sonde", "Actions"),
                contentRow = { temperature ->
                    MainRowContent(
                        temperature,
                        onClickDetailButton = { date, readingDeviceName ->
                            navController.navigate("gridDetail/${date}/${readingDeviceName.name}")
                        }
                    )
                })
//            TableComponent(temperatures, loading) { date, readingDeviceName ->
//                viewModel.updateTemperatureDetailSearchParam(date, readingDeviceName)
//                navController.navigate(MainNavDestination.GridDetail.route)
//            }
        }
    }
}

@Composable
fun MainRowContent(
    temperature: TemperatureDto,
    onClickDetailButton: (date: LocalDate, readingDevice: ReadingDeviceName) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val modifier = Modifier.weight(2f)
        val height = 45.dp

        TextCellComponent(
            "${BigDecimal(temperature.temperature).setScale(2, RoundingMode.HALF_UP)}",
            modifier,
            height
        )
        TextCellComponent(
            temperature.collectionDate.format(
                DateTimeFormatter.ofPattern(
                    "dd / MM / yyyy"
                )
            ), modifier, height
        )
        TextCellComponent(
            temperature.readingDeviceName.value,
            modifier,
            height
        )
        ButtonCellComponent(
            "Détail",
            icon = Icons.Default.RemoveRedEye,
            iconContentDescription = "Oeil",
            onClick = {
                onClickDetailButton(
                    temperature.collectionDate.toLocalDate(),
                    temperature.readingDeviceName
                )
            },
            modifier = modifier,
            height = 35.dp
        )
    }
}
