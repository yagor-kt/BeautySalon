package com.example.beautysalon.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.beautysalon.ui.theme.Error
import com.example.beautysalon.ui.theme.Muted
import com.example.beautysalon.ui.theme.Success
import com.example.beautysalon.ui.theme.Warning

@Composable
fun statusColor(status: String): Color = when (status) {
    "PENDING" -> Muted
    "DONE" -> Success
    "CANCELLED" -> Error
    else -> Muted
}

@Composable
fun loadColor(percent: Float): Color = when {
    percent < 0.5f -> Success
    percent <= 0.8f -> Warning
    else -> Error
}