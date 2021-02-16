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
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.example.stocksapp.ui.components.TickerCard
import com.example.stocksapp.ui.components.charts.line.LineChartData
import com.example.stocksapp.ui.screens.NavigableScreens
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import kotlin.random.Random

@Composable
fun Home(
    viewModel: HomeViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    // TODO bind viewModel data and events to arguments/callbacks like the codelab
    // https://github.com/googlecodelabs/android-compose-codelabs/blob/main/StateCodelab/finished/src/main/java/com/codelabs/state/todo/TodoActivity.kt
    // https://github.com/googlecodelabs/android-compose-codelabs/blob/main/StateCodelab/finished/src/main/java/com/codelabs/state/todo/TodoScreen.kt
    HomeScreen(
        modifier = modifier,
        openStockDetail = { symbol ->
            navController.navigate(NavigableScreens.StockDetail.buildRoute(symbol))
        }
    )
}

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
