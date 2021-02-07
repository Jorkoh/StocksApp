package com.example.stocksapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import dev.chrisbanes.accompanist.insets.statusBarsPadding

@Composable
fun HomeScreen(modifier: Modifier){
    Surface(modifier.fillMaxSize()) {
        Text(
            "Home destination",
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxHeight()
                .wrapContentSize()
        )
    }
}