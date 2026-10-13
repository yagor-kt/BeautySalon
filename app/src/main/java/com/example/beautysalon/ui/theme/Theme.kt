package com.example.beautysalon.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SalonDarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurface,
    error = Error,
    onError = OnPrimary
)

@Composable
fun BeautySalonTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SalonDarkColorScheme,
        typography = Typography,
        content = content
    )
}