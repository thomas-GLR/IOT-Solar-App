package com.example.solariotmobile.ui.components.tableComponent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.ui.components.ButtonWithIconAndText

@Composable
fun ButtonCellComponent(
    title: String,
    icon: ImageVector,
    iconContentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 38.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        ButtonWithIconAndText(
            onClickButton = { onClick() },
            text = title,
            icon = icon,
            iconContentDescription = iconContentDescription,
            modifier = Modifier
                .height(height)
                .wrapContentHeight()
                .padding(end = 8.dp)
        )
    }
}