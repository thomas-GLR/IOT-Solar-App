package com.example.solariotmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.solariotmobile.ui.theme.FirstGreenForGradient

@Composable
fun ButtonWithIconAndText(
    onClickButton: () -> Unit,
    text: String,
    icon: ImageVector,
    iconContentDescription: String,
    modifier: Modifier = Modifier,
    roundedCornerShapeSize: Dp = 20.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Button(
            onClick = { onClickButton() },
            colors = ButtonDefaults.buttonColors(
                containerColor = FirstGreenForGradient,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(roundedCornerShapeSize)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription,
                    tint = Color.White,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text,
                    color = Color.White,
                )
            }
        }
    }
}