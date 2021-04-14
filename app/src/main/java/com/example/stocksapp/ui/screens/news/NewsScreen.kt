package com.example.stocksapp.ui.screens.news

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.stocksapp.R
import com.example.stocksapp.data.model.News
import com.example.stocksapp.ui.components.ListItemState
import com.example.stocksapp.ui.components.NewsListItem
import com.example.stocksapp.ui.components.SectionTitle
import com.google.accompanist.insets.statusBarsPadding

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
        onReadMoreClicked = { news ->
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(news.url)))
        }
    )
}

@Composable
fun NewsContent(
    newsUIState: State<NewsUIState>,
    modifier: Modifier = Modifier,
    onReadMoreClicked: (News) -> Unit
) {
    var expandedNews by remember { mutableStateOf<Long?>(null) }

    LazyColumn(modifier.fillMaxSize()) {
        item { Spacer(modifier = Modifier.statusBarsPadding()) }
        item { SectionTitle(stringResource(R.string.news_section_title)) }
        when (val state = newsUIState.value) {
            is NewsUIState.Loading -> item { Text("LOADING") }
            is NewsUIState.Error -> item { Text("ERROR ${state.message}") }
            is NewsUIState.Success -> {
                items(state.news) { news ->
                    NewsListItem(
                        news = news,
                        itemState = if (expandedNews == news.id) {
                            ListItemState.Expanded
                        } else {
                            ListItemState.Collapsed
                        },
                        onClick = { expandedNews = if (expandedNews == news.id) null else news.id },
                        onReadMoreClicked = { onReadMoreClicked(news) }
                    )
                }
            }
        }
    }
}
