package com.example.stocksapp.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stocksapp.ui.components.charts.line.LineChart
import com.example.stocksapp.ui.components.charts.line.LineChartData
import com.example.stocksapp.ui.components.charts.line.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import java.util.*
import kotlin.random.Random

@Composable
fun TickerCard(
    symbol: String,
    chartData: LineChartData,
    modifier: Modifier = Modifier
) {
    Card(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("$${symbol.toUpperCase(Locale.getDefault())}")
            LineChart(
                lineChartData = chartData,
                animation = TweenSpec(10000, easing = LinearOutSlowInEasing),
                lineDrawer = SolidLineDrawer(color = Color.Green)
            )
        }
    }
}

@Preview
@Composable
fun TickerCardPreview() {
    TickerCard(
        symbol = "GME",
        chartData = LineChartData(
            points = (1..15).map { LineChartData.Point(randomYValue(), "Label $it") }
        )
    )
}

private fun randomYValue() = Random.nextDouble(5.0, 20.0).toFloat()