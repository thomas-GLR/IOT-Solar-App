package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.breens.beetablescompose.BeeTablesCompose
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.data.TemperatureDto
import com.example.solariotmobile.ui.components.FailureWithRefresh
import com.example.solariotmobile.ui.components.Loading
import com.example.solariotmobile.ui.components.TableComponent
import com.example.solariotmobile.viewmodel.TemperaturesViewModel
import java.time.format.DateTimeFormatter

@Composable
fun GridScreen(viewModel: TemperaturesViewModel = hiltViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var loading by remember { mutableStateOf(false) }
    var failure by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var temperatures by remember { mutableStateOf(emptyList<TemperatureDto>()) }

    viewModel.isLoading.observe(lifecycleOwner) { isLoading ->
        loading = isLoading
    }
    viewModel.isFailure.observe(lifecycleOwner) { isFailure ->
        failure = isFailure
    }
    viewModel.getMessage.observe(lifecycleOwner) { getMessage ->
        message = getMessage
    }
    viewModel.getTemperaturesFiltered.observe(lifecycleOwner) { getTemperatures ->
        temperatures = getTemperatures
    }

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    if (loading) {
        Loading()
    }
    if (failure) {
        FailureWithRefresh(message) { viewModel.fetchData() }
    } else {

        // https://developer.android.com/develop/ui/compose/lists?hl=fr

//        val scrollState = rememberScrollState()
//
//        Column(
//            modifier = Modifier.verticalScroll(scrollState)
//        ) {
//            FilterMenu(viewModel)
//            val tableHeaders = listOf("ID", "Températures", "Date", "Sonde")
//
//            BeeTablesCompose(
//                data = temperatures,
//                headerTableTitles = tableHeaders,
//                headerTitlesBorderColor = Color.LightGray,
//                headerTitlesTextStyle = TextStyle(fontSize = 14.sp),
//                headerTitlesBackGroundColor = Color.White,
//                tableRowColors = listOf(Color.White, Color.White),
//                rowBorderColor = Color.LightGray,
//                rowTextStyle = TextStyle(fontSize = 12.sp),
//                tableElevation = 6.dp,
//            )
//        }

        Column {
            FilterMenu(viewModel)
            TableComponent(
                temperatures
            )
        }
    }
}
