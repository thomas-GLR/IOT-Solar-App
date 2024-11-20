package com.example.solariotmobile.ui.grid

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GridScreen() {
    LazyColumn {
        items(5) { index ->
            Text(text = "Item: $index")
        }
    }
}
