package com.example.stocksapp.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stocksapp.R
import com.example.stocksapp.ui.components.LoadingIndicator
import com.example.stocksapp.ui.components.QuoteListItem
import com.example.stocksapp.ui.components.SectionTitle
import com.example.stocksapp.ui.components.TickerCardPreview
import com.example.stocksapp.ui.screens.NavigableScreen
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    HomeContent(
        activeSymbolsUIState = viewModel.activeSymbolsUIState.collectAsState(),
        modifier = modifier,
        onSymbolSelected = { symbol ->
            navController.navigate(NavigableScreen.StockDetail.buildRoute(symbol))
        }
    )
}

@Composable
fun HomeContent(
    activeSymbolsUIState: State<ActiveSymbolsUIState>,
    modifier: Modifier = Modifier,
    onSymbolSelected: (String) -> Unit
) {
    LazyColumn(modifier.fillMaxSize()) {
        item { Spacer(modifier = Modifier.statusBarsPadding()) }
        // userSymbolsSection(activeSymbolsUIState, onSymbolSelected)
        activeSymbolsSection(activeSymbolsUIState, onSymbolSelected)
    }
}

// TODO pass actual state and quotes with charts here
private fun LazyListScope.userSymbolsSection(
    userSymbolsUIState: State<ActiveSymbolsUIState>,
    onSymbolSelected: (String) -> Unit,
) {
    item { SectionTitle(stringResource(R.string.user_symbols_section_title)) }
    when (val state = userSymbolsUIState.value) {
        is ActiveSymbolsUIState.Loading -> item { LoadingIndicator(Modifier.padding(vertical = 24.dp)) }
        is ActiveSymbolsUIState.Error -> item { Text(state.message) }
        is ActiveSymbolsUIState.Success -> item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = state.quotes,
                    key = { it.symbol }
                ) { quote ->
                    TickerCardPreview()
                }
            }
        }
    }
}

private fun LazyListScope.activeSymbolsSection(
    activeSymbolsUIState: State<ActiveSymbolsUIState>,
    onSymbolSelected: (String) -> Unit,
) {
    item { SectionTitle(stringResource(R.string.active_symbols_section_title)) }
    when (val state = activeSymbolsUIState.value) {
        is ActiveSymbolsUIState.Loading -> item { LoadingIndicator(Modifier.padding(top = 24.dp)) }
        is ActiveSymbolsUIState.Error -> item { Text(state.message) }
        is ActiveSymbolsUIState.Success -> {
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
                    onSymbolSelected = onSymbolSelected,
                    modifier = Modifier.alpha(alpha.value)
                )
            }
        }
    }
}
