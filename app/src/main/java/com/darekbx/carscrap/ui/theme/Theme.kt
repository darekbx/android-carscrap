package com.darekbx.carscrap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    background = Color.Black,
    onBackground = Color(0xFFEEEEEE),
    surface = Color(0xFFEEEEEE),
    onSurface = Color.LightGray,
    surfaceContainer = Color(0xFF282828),
    primary = Green,
    secondary = Color.Black,
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