package com.example.solariotmobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.ui.components.ButtonWithIconAndText
import com.example.solariotmobile.ui.components.DateRangePickerModal
import com.example.solariotmobile.ui.theme.FirstGreenForGradient
import com.example.solariotmobile.viewmodel.TemperaturesViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun FilterMenu(
    viewModel: TemperaturesViewModel
) {
    val readingDevices = ReadingDeviceName.entries.toList()

    val selectedReadingDeviceName by viewModel.getSelectedReadingDeviceNames.collectAsState()
    val selectedDateRange by viewModel.getSelectedDateRange.collectAsState()

    var showRangeModal by remember { mutableStateOf(false) }

    val datesRangeInformation =
        if (selectedDateRange != null to null && selectedDateRange.first != null && selectedDateRange.second != null)
            "${
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(selectedDateRange.first!!),
                    ZoneOffset.UTC
                ).toLocalDate().format(
                    DateTimeFormatter.ofPattern("dd / MM / yyyy")
                )
            } - ${
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(selectedDateRange.second!!),
                    ZoneOffset.UTC
                ).toLocalDate().format(
                    DateTimeFormatter.ofPattern("dd / MM / yyyy")
                )
            }" else "Aucune dates sélectionnées"

    if (showRangeModal) {
        DateRangePickerModal(
            onDateRangeSelected = {
                viewModel.toggleDateRange(it)
                showRangeModal = false
            },
            onDismiss = { showRangeModal = false }
        )
    }

    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Button(
            onClick = { showRangeModal = !showRangeModal },
            modifier = Modifier
                .width(325.dp),
            colors = buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Text(datesRangeInformation)
                VerticalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 25.dp)
                )
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Calendrier"
                )
            }
        }

        val cornerRadius = 8.dp

        MultiChoiceSegmentedButtonRow {
            readingDevices.forEachIndexed { index, readingDeviceKey ->
                val shape = when (index) {
                    0 -> RoundedCornerShape(
                        topStart = cornerRadius,
                        bottomStart = cornerRadius,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    )
                    readingDevices.lastIndex -> RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = cornerRadius,
                        bottomEnd = cornerRadius
                    )
                    else -> RoundedCornerShape(0.dp)
                }

                SegmentedButton(
                    icon = {},
                    shape = shape,
//                    shape = SegmentedButtonDefaults.itemShape(
//                        index = index,
//                        count = readingDevices.size
//                    ),
                    onCheckedChange = {
                        viewModel.toggleReadingDevice(readingDeviceKey)
                    },
                    checked = readingDeviceKey in selectedReadingDeviceName,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = FirstGreenForGradient,
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = Color.Black,
                        activeBorderColor = Color.LightGray,
                        inactiveBorderColor = Color.LightGray,
                    ),
                ) {
                    Text(
                        text = readingDeviceKey.value
                    )
                }
            }
        }
        ButtonWithIconAndText(
            onClickButton = {
                viewModel.resetFilters()
            },
            text = "Réinitialiser les filtres",
            icon = Icons.Filled.Restore,
            iconContentDescription = "reset",
            roundedCornerShapeSize = cornerRadius
        )
    }
}