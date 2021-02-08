package com.example.stocksapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.stocksapp.ui.components.TickerCard
import com.example.stocksapp.ui.components.TickerCardPreview
import dev.chrisbanes.accompanist.insets.statusBarsPadding

@Composable
fun HomeScreen(modifier: Modifier) {
    Surface(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Home destination",
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )
            TickerCardPreview()
        }
    }
}