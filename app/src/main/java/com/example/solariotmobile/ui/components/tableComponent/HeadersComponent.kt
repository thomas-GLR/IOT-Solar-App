package com.example.solariotmobile.ui.components.tableComponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun HeadersComponent(
    headers: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
        ) {
            headers.forEach { title ->
                Box(
                    modifier = Modifier
                        .weight(2f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(end = 8.dp)
                    )
                }
            }
        }
        HorizontalDivider(
            color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth(0.9f),
            thickness = 1.dp
        )
    }
}