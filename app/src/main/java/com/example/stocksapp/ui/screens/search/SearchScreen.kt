package com.example.stocksapp.ui.screens.search

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Surface(modifier.fillMaxSize()) {
        Text(
            "Search destination",
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxHeight()
                .wrapContentSize()
        )
    }
}
