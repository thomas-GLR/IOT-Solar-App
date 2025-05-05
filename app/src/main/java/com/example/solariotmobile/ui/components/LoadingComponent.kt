package com.example.solariotmobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.ui.theme.FirstGreenForGradient

@Composable
fun LoadingComponent(indicatorSize: Dp = 64.dp) {
    Box(modifier = Modifier.fillMaxWidth()) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(indicatorSize)
                .align(Alignment.Center),
            color = FirstGreenForGradient,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}