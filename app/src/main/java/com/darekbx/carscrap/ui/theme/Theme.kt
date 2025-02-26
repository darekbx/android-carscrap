package com.darekbx.carscrap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    background = Color.Black,
    onBackground = Color(0xFFEEEEEE),
    surface = Color(0xFFEEEEEE),
    onSurface = Color.Black,
    primary = Green,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

@Composable
fun CarScrapTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}