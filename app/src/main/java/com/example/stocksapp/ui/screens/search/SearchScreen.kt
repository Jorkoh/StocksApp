package com.example.stocksapp.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stocksapp.R
import com.example.stocksapp.ui.components.SectionTitle
import com.example.stocksapp.ui.components.SymbolListItem
import com.example.stocksapp.ui.screens.NavigableScreen
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    SearchContent(
        searchUIState = viewModel.searchUIState.collectAsState(),
        modifier = modifier,
        onQueryChanged = viewModel::onQueryChanged,
        onSymbolCLicked = { symbol ->
            navController.navigate(NavigableScreen.StockDetail.buildRoute(symbol))
        }
    )
}

@Composable
fun SearchContent(
    searchUIState: State<SearchUIState>,
    modifier: Modifier,
    onQueryChanged: (String) -> Unit,
    onSymbolCLicked: (String) -> Unit
) {
    LazyColumn(modifier.fillMaxSize()) {
        item { Spacer(modifier = Modifier.statusBarsPadding()) }
        item { SectionTitle(stringResource(R.string.search_section_title)) }
        item { SearchField(query = searchUIState.value.query, onQueryChanged = onQueryChanged) }
        when (val state = searchUIState.value) {
            is SearchUIState.Error -> item { Text("ERROR") }
            is SearchUIState.InUse -> items(state.results) { symbol ->
                SymbolListItem(
                    symbol = symbol,
                    onClick = { onSymbolCLicked(symbol.symbol) }
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        OutlinedTextField(
            value = query,
            singleLine = true,
            label = { Text(stringResource(id = R.string.search_field_label)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = MaterialTheme.colors.onBackground,
                focusedBorderColor = MaterialTheme.colors.onSurface,
                focusedLabelColor = MaterialTheme.colors.onSurface
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search)
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clear),
                        contentDescription = stringResource(R.string.clear),
                        modifier = Modifier.clickable { onQueryChanged("") }
                    )
                }
            },
            onValueChange = onQueryChanged
        )
    }
}
