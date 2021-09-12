package com.example.stocksapp.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = black,
    primaryVariant = offBlack,
    secondary = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black
)

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = Color.White,
    primaryVariant = offWhite,
    secondary = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    surface = offWhite,
    background = offWhite
)

val Colors.profit: Color
    get() = green

val Colors.loss: Color
    get() = red

@Composable
fun StocksAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
