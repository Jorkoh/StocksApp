package com.example.stocksapp.ui.screens.stockdetail

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stocksapp.MainActivity
import dagger.hilt.android.EntryPointAccessors
import dev.chrisbanes.accompanist.insets.statusBarsPadding

@Composable
fun StockDetail(
    viewModel: StockDetailViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // TODO bind viewModel data and events to arguments/callbacks like the codelab
    // https://github.com/googlecodelabs/android-compose-codelabs/blob/main/StateCodelab/finished/src/main/java/com/codelabs/state/todo/TodoActivity.kt
    // https://github.com/googlecodelabs/android-compose-codelabs/blob/main/StateCodelab/finished/src/main/java/com/codelabs/state/todo/TodoScreen.kt
    StockDetailScreen(
        modifier = modifier
    )
}

@Composable
fun StockDetailScreen(modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxSize()) {
        Text(
            "Stock detail destination",
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxHeight()
                .wrapContentSize()
        )
    }
}

@Composable
fun stockDetailViewModel(symbol: String): StockDetailViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).stockDetailViewModelFactory()

    return viewModel(factory = StockDetailViewModel.provideFactory(factory, symbol))
}