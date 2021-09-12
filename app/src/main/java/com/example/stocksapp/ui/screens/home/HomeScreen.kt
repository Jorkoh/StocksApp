package com.example.stocksapp.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.stocksapp.ui.screens.NavigableScreen
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
            navController.navigate(NavigableScreen.StockDetail.buildRoute(symbol))
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
    when (val state = trackedSymbolsUIState.value) {
        is TrackedSymbolsUIState.Loading -> item { PlaceholderUserTrackedSymbols() }
        is TrackedSymbolsUIState.Error -> item { Text(state.message) }
        is TrackedSymbolsUIState.Success -> item {
            if (state.chartPrices.isEmpty()) {
                EmptyUserTrackedSymbols()
            } else {
                UserTrackedSymbols(state, onSymbolSelected)
            }
        }
    }
}

@Composable
private fun PlaceholderUserTrackedSymbols() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(5) { QuoteWithChartCardPlaceholder() }
    }
}

@Composable
private fun EmptyUserTrackedSymbols() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
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

@Composable
private fun UserTrackedSymbols(
    state: TrackedSymbolsUIState.Success,
    onSymbolSelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = state.chartPrices,
            key = { it.first.symbol }
        ) { quoteAndChartPrices ->
            QuoteWithChartCard(
                quote = quoteAndChartPrices.first,
                chartData = LineChartData(quoteAndChartPrices.second.map {
                    LineChartData.Point(it.closePrice.toFloat(), it.date.toString())
                }),
                onSymbolSelected = onSymbolSelected
            )
        }
    }
}

private fun LazyListScope.activeQuotesSection(
    activeSymbolsUIState: State<ActiveSymbolsUIState>,
    onSymbolSelected: (String) -> Unit,
) {
    item { SectionTitle(stringResource(R.string.active_symbols_section_title)) }
    when (val state = activeSymbolsUIState.value) {
        is ActiveSymbolsUIState.Loading -> {
            placeholderActiveQuotes()
        }
        is ActiveSymbolsUIState.Error -> item { Text(state.message) }
        is ActiveSymbolsUIState.Success -> {
            activeQuotes(state, onSymbolSelected)
        }
    }
}

private fun LazyListScope.placeholderActiveQuotes() {
    items(MOST_ACTIVE_COUNT) { QuoteListItemPlaceholder() }
}

private fun LazyListScope.activeQuotes(
    state: ActiveSymbolsUIState.Success,
    onSymbolSelected: (String) -> Unit
) {
    items(
        items = state.quotes,
        key = { it.symbol }
    ) { quote ->
        val alpha = remember { Animatable(0f) }
        LaunchedEffect(alpha) {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 350, easing = LinearOutSlowInEasing)
            )
        }

        QuoteListItem(
            quote = quote,
            onClick = { onSymbolSelected(quote.symbol) },
            modifier = Modifier.alpha(alpha.value)
        )
    }
}
