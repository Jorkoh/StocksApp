package com.example.stocksapp.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.example.stocksapp.R
import com.example.stocksapp.ui.components.LoadingIndicator
import com.example.stocksapp.ui.components.QuoteListItem
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
    LazyColumn(modifier.fillMaxSize().statusBarsPadding()) {
        activeSymbolsSection(activeSymbolsUIState, onSymbolSelected)
    }
}

private fun LazyListScope.activeSymbolsSection(
    activeSymbolsUIState: State<ActiveSymbolsUIState>,
    onSymbolSelected: (String) -> Unit,
) {
    item { HomeScreenSectionTitle(stringResource(R.string.active_symbols_section_title)) }
    when (val state = activeSymbolsUIState.value) {
        is ActiveSymbolsUIState.Loading -> item { LoadingIndicator(Modifier.padding(top = 24.dp)) }
        is ActiveSymbolsUIState.Error -> item { Text(state.message) }
        is ActiveSymbolsUIState.Success -> {
            items(
                items = state.quotes,
                key = { it.symbol }
            ) { quote ->
                QuoteListItem(quote, onSymbolSelected)
            }
        }
    }
}

@Composable
fun HomeScreenSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
        modifier = modifier.padding(start = 24.dp, bottom = 12.dp, top = 24.dp)
    )
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
