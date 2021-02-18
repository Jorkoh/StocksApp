package com.example.stocksapp.ui.screens.stockdetail

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
    StockDetailScreen(
        companyInfoUIState = viewModel.companyInfoUIState.collectAsState(),
        modifier = modifier,
        onUpButtonPressed = { navController.navigateUp() }
    )
}

@Composable
fun StockDetailScreen(
    companyInfoUIState: State<CompanyInfoUIState>,
    modifier: Modifier = Modifier,
    onUpButtonPressed: () -> Unit
) {
    Surface(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Stock detail destination",
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )

            Text(
                text = when (val state = companyInfoUIState.value) {
                    CompanyInfoUIState.Loading -> "LOADING..."
                    is CompanyInfoUIState.Success -> "SUCCESS: ${state.companyInfo.companyName}"
                    is CompanyInfoUIState.Error -> "ERROR: ${state.message}"
                },
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
        }
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