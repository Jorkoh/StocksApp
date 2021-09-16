package com.example.stocksapp.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = offBlack,
    primaryVariant = black,
    secondary = white,
    surface = offBlack,
    background = black,
    onPrimary = white,
    onSecondary = black,
)

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = white,
    primaryVariant = offWhite,
    secondary = black,
    surface = white,
    background = offWhite,
    onPrimary = black,
    onSecondary = white,
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
