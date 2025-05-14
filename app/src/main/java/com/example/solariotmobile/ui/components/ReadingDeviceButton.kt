package com.example.solariotmobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.data.ReadingDeviceName
import com.example.solariotmobile.ui.theme.FirstGreenForGradient

@Composable
fun ReadingDeviceButton(
    selectTopDevice: () -> Unit,
    selectMiddleDevice: () -> Unit,
    selectBottomDevice: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        var selectedIndex by remember { mutableIntStateOf(0) }
        val options = listOf(
            ReadingDeviceName.TOP,
            ReadingDeviceName.MIDDLE,
            ReadingDeviceName.BOTTOM)

        val cornerRadius = 8.dp

        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, device ->

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
                        when (options[selectedIndex]) {
                            ReadingDeviceName.TOP -> selectTopDevice()
                            ReadingDeviceName.MIDDLE -> selectMiddleDevice()
                            ReadingDeviceName.BOTTOM -> selectBottomDevice()
                        }
                    },
                    selected = index == selectedIndex,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = FirstGreenForGradient.copy(alpha = 0.8f),
                        activeContentColor = Color.Black,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = Color.Black
                    ),
                ) {
                    Text(device.value)
                }
            }
        }
    }
}