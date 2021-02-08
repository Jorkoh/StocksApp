package com.example.stocksapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.stocksapp.ui.components.charts.line.LineChart
import com.example.stocksapp.ui.components.charts.line.LineChartData
import java.util.*

@Composable
fun TickerCard(
    symbol: String,
    chartData: LineChartData,
    modifier: Modifier = Modifier
) {
    Card(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("$${symbol.toUpperCase(Locale.getDefault())}")
            LineChart(lineChartData = chartData)
        }
    }
}

@Composable
fun TickerCardPreview(){
    TickerCard(
        symbol = "GME",
        chartData = LineChartData(
            points = listOf(
                LineChartData.Point(randomYValue(), "Label1"),
                LineChartData.Point(randomYValue(), "Label2"),
                LineChartData.Point(randomYValue(), "Label3"),
                LineChartData.Point(randomYValue(), "Label4"),
                LineChartData.Point(randomYValue(), "Label5"),
                LineChartData.Point(randomYValue(), "Label6"),
                LineChartData.Point(randomYValue(), "Label7")
            )
        )
    )
}

private fun randomYValue(): Float = (100f * Math.random()).toFloat() + 45f