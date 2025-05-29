package com.example.solariotmobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.data.AggregationType
import com.example.solariotmobile.ui.theme.FirstGreenForGradient

@Composable
fun TimeRangeButton(
    selectTimeRange: (timeRangeSelected: AggregationType) -> Unit
) {
    Row(
//        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        var selectedIndex by remember { mutableIntStateOf(2) }
        val options = listOf(
            AggregationType.HOURS,
            AggregationType.DAYS,
            AggregationType.MONTHS,
            AggregationType.YEARS,
        )

        val cornerRadius = 8.dp

        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, timeRange ->

                val shape = when (index) {
                    0 -> RoundedCornerShape(
                        topStart = cornerRadius,
                        bottomStart = cornerRadius,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    )
                    options.lastIndex -> RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = cornerRadius,
                        bottomEnd = cornerRadius
                    )
                    else -> RoundedCornerShape(0.dp)
                }

                SegmentedButton(
                    shape = shape,
                    icon = {},
                    onClick = {
                        selectedIndex = index
                        selectTimeRange(options[selectedIndex])
                    },
                    selected = index == selectedIndex,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = FirstGreenForGradient,
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = Color.Black,
                        activeBorderColor = Color.LightGray,
                        inactiveBorderColor = Color.LightGray,
                    ),
                ) {
                    Text(timeRange.value)
                }
            }
        }
    }
}