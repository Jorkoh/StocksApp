package com.example.stocksapp.ui.screens.stockdetail

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stocksapp.MainActivity
import com.example.stocksapp.R
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
    Scaffold(
        modifier = modifier,
        topBar = { StockDetailTopBar(companyInfoUIState, onUpButtonPressed) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (val state = companyInfoUIState.value) {
                    is CompanyInfoUIState.Loading -> "LOADING..."
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
fun StockDetailTopBar(
    companyInfoUIState: State<CompanyInfoUIState>,
    onUpButtonPressed: () -> Unit
) {
    TopAppBar(modifier = Modifier.statusBarsPadding()) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { onUpButtonPressed() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.label_back)
                )
            }
            Text(
                text = when (val state = companyInfoUIState.value) {
                    is CompanyInfoUIState.Success -> state.companyInfo.symbol
                    else -> ""
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.Center)
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