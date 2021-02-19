package com.example.stocksapp.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.example.stocksapp.data.model.Quote
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
    HomeScreen(
        activeSymbolsUIState = viewModel.activeSymbolsUIState.collectAsState(),
        modifier = modifier,
        onSymbolSelected = { symbol ->
            navController.navigate(NavigableScreens.StockDetail.buildRoute(symbol))
        }
    )
}

@Composable
fun HomeScreen(
    activeSymbolsUIState: State<ActiveSymbolsUIState>,
    modifier: Modifier = Modifier,
    onSymbolSelected: (String) -> Unit
) {
    Surface(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActiveSymbols(activeSymbolsUIState, onSymbolSelected)
        }
    }
}

@Composable
fun ActiveSymbols(
    activeSymbolsUIState: State<ActiveSymbolsUIState>,
    onSymbolSelected: (String) -> Unit
) {
    when (val state = activeSymbolsUIState.value) {
        is ActiveSymbolsUIState.Loading -> Text("LOADING")
        is ActiveSymbolsUIState.Error -> Text(state.message)
        is ActiveSymbolsUIState.Success -> LazyColumn {
            items(
                items = state.quotes,
                key = { it.symbol },
                itemContent = { SymbolListItem(it, onSymbolSelected) },
            )
        }
    }

}

@Composable
fun SymbolListItem(
    quote: Quote,
    onSymbolSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = { onSymbolSelected(quote.symbol) })
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(quote.companyName)
            Text(quote.symbol)
        }
        Column {
            Text("${quote.latestPrice}")
            Text("${quote.change}%")
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
        // TODO: this won't work on landscape btw
        modifier = Modifier.fillMaxWidth(0.8f)
    )
}

private fun randomYValue() = Random.nextDouble(5.0, 20.0).toFloat()
