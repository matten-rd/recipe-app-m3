package com.example.minarecept.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Pink50,
    primaryVariant = DarkGrey,
    secondary = Brown700,
    background = Black,
    surface = Black,
    onPrimary = Brown700,
    onSecondary = Pink50,
    onBackground = White,
    onSurface = White
)


private val LightColorPalette = lightColors(
    primary = Brown700,
    primaryVariant = LightGrey,
    secondary = Pink50,
    background = White,
    surface = White,
    onPrimary = Pink50,
    onSecondary = Brown700,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun MinaReceptTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}