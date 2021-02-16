package com.example.stocksapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.stocksapp.ui.components.TickerCard
import com.example.stocksapp.ui.components.charts.line.LineChartData
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import kotlin.random.Random

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    openStockDetail: (String) -> Unit
) {
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
            Button(onClick = { openStockDetail("GME") }) {
                Text("Open stock detail screen")
            }
            TickerCardTest()
        }
    }
}

@Composable
fun TickerCardTest() {
    TickerCard(
        symbol = "GME",
        chartData = LineChartData(
            points = (1..15).map { LineChartData.Point(randomYValue(), "#$it") }
        ),
        // TODO this won't work on landscape btw
        modifier = Modifier.fillMaxWidth(0.8f)
    )
}

private fun randomYValue() = Random.nextDouble(5.0, 20.0).toFloat()
