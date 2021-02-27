package com.example.stocksapp.ui.screens.news

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun NewsScreen(
    viewModel: NewsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    NewsContent(
        newsUIState = viewModel.newsUIState.collectAsState(),
        modifier = modifier,
        onNewsClicked = { url ->
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    )
}

@Composable
fun NewsContent(
    newsUIState: State<NewsUIState>,
    modifier: Modifier = Modifier,
    onNewsClicked: (String) -> Unit
) {
    LazyColumn(modifier.fillMaxSize()) {
        when (val state = newsUIState.value) {
            is NewsUIState.Loading -> item { Text("LOADING") }
            is NewsUIState.Error -> item { Text("ERROR ${state.message}") }
            is NewsUIState.Success -> {
                items(state.news) { news ->
                    Text(news.headline, modifier = Modifier.clickable { onNewsClicked(news.url) })
                }
            }
        }
    }
}