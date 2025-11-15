package com.example.solariotmobile.ui.components.tableComponent


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TextCellComponent(
    content: String,
    modifier: Modifier = Modifier,
    height: Dp = 38.dp,
    textColor: Color = LocalContentColor.current
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = content,
            color = textColor,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .height(height)
                .wrapContentHeight()
                .padding(end = 8.dp)
        )
    }
}