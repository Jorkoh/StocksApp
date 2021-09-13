package com.example.stocksapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stocksapp.R
import com.example.stocksapp.data.repositories.stocks.StocksRepository.Companion.MOST_ACTIVE_COUNT
import com.example.stocksapp.ui.components.QuoteListItem
import com.example.stocksapp.ui.components.QuoteListItemPlaceholder
import com.example.stocksapp.ui.components.QuoteWithChartCard
import com.example.stocksapp.ui.components.QuoteWithChartCardPlaceholder
import com.example.stocksapp.ui.components.SectionTitle
import com.example.stocksapp.ui.components.charts.line.LineChartData
import com.example.stocksapp.ui.components.fadingItem
import com.example.stocksapp.ui.components.fadingItems
import com.example.stocksapp.ui.screens.Screen
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    HomeContent(
        trackedSymbolsUIState = viewModel.trackedSymbolsUIState.collectAsState(),
        activeSymbolsUIState = viewModel.activeSymbolsUIState.collectAsState(),
        modifier = modifier,
        onSymbolSelected = { symbol ->
            navController.navigate(Screen.StockDetail.buildRoute(symbol))
        }
    )
}

@Composable
fun HomeContent(
    trackedSymbolsUIState: State<TrackedSymbolsUIState>,
    activeSymbolsUIState: State<ActiveSymbolsUIState>,
    modifier: Modifier = Modifier,
    onSymbolSelected: (String) -> Unit
) {
    LazyColumn(modifier.fillMaxSize()) {
        item { Spacer(modifier = Modifier.statusBarsPadding()) }
        userTrackedSymbolsSection(trackedSymbolsUIState, onSymbolSelected)
        activeQuotesSection(activeSymbolsUIState, onSymbolSelected)
    }
}

private fun LazyListScope.userTrackedSymbolsSection(
    trackedSymbolsUIState: State<TrackedSymbolsUIState>,
    onSymbolSelected: (String) -> Unit,
) {
    item { SectionTitle(stringResource(R.string.user_symbols_section_title)) }
    item {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (val state = trackedSymbolsUIState.value) {
                is TrackedSymbolsUIState.Loading -> fadingItems(5, delayDurationMillis = 500) { _, alphaModifier ->
                    QuoteWithChartCardPlaceholder(alphaModifier)
                }
                is TrackedSymbolsUIState.Error -> item { Text(state.message) }
                is TrackedSymbolsUIState.Success -> {
                    if (state.chartPrices.isEmpty()) {
                        fadingItem { alphaModifier -> EmptyUserTrackedSymbols(alphaModifier) }
                    } else {
                        fadingItems(
                            items = state.chartPrices,
                            key = { it.first.symbol }
                        ) { quoteAndChartPrices, alphaModifier ->
                            QuoteWithChartCard(
                                quote = quoteAndChartPrices.first,
                                chartData = LineChartData(quoteAndChartPrices.second.map {
                                    LineChartData.Point(it.closePrice.toFloat(), it.date.toString())
                                }),
                                modifier = alphaModifier,
                                onSymbolSelected = onSymbolSelected
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyUserTrackedSymbols(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.padding(top = 16.dp, bottom = 16.dp, start = 24.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_tracked),
            contentDescription = stringResource(id = R.string.track),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.user_symbols_section_suggestion),
            style = MaterialTheme.typography.h6.copy(fontSize = 18.sp)
        )
    }
}

private fun LazyListScope.activeQuotesSection(
    activeSymbolsUIState: State<ActiveSymbolsUIState>,
    onSymbolSelected: (String) -> Unit,
) {
    item { SectionTitle(stringResource(R.string.active_symbols_section_title)) }
    when (val state = activeSymbolsUIState.value) {
        is ActiveSymbolsUIState.Loading -> {
            fadingItems(MOST_ACTIVE_COUNT, delayDurationMillis = 500) { _, alphaModifier ->
                QuoteListItemPlaceholder(modifier = alphaModifier)
            }
        }
        is ActiveSymbolsUIState.Error -> item { Text(state.message) }
        is ActiveSymbolsUIState.Success -> {
            fadingItems(
                items = state.quotes,
                key = { it.symbol }
            ) { quote, alphaModifier ->
                QuoteListItem(
                    quote = quote,
                    onClick = { onSymbolSelected(quote.symbol) },
                    modifier = alphaModifier
                )
            }
        }
    }
}

