package com.example.stocksapp.ui.screens.stockdetail

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stocksapp.MainActivity
import com.example.stocksapp.R
import com.example.stocksapp.data.repositories.stocks.ChartRange
import com.example.stocksapp.ui.components.CustomChartRangeSelector
import com.example.stocksapp.ui.components.LoadingIndicator
import com.example.stocksapp.ui.components.charts.line.LineChart
import com.example.stocksapp.ui.components.charts.line.renderer.line.SolidLineDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.path.BezierLinePathCalculator
import com.example.stocksapp.ui.components.charts.line.renderer.xaxis.NoXAxisDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.yaxis.NoYAxisDrawer
import com.google.accompanist.insets.statusBarsPadding
import dagger.hilt.android.EntryPointAccessors

@Composable
fun StockDetailScreen(
    viewModel: StockDetailViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    StockDetailContent(
        stockDetailUIState = viewModel.stockDetailUIState.collectAsState(),
        modifier = modifier,
        onUpButtonPressed = { navController.navigateUp() },
        onTrackButtonPressed = { viewModel.toggleIsTracked() },
        onChartRangeSelected = { viewModel.changeChartRange(it) }
    )
}

@Composable
fun StockDetailContent(
    stockDetailUIState: State<StockDetailUIState>,
    modifier: Modifier = Modifier,
    onUpButtonPressed: () -> Unit,
    onTrackButtonPressed: () -> Unit,
    onChartRangeSelected: (ChartRange) -> Unit
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            StockDetailTopBar(
                stockDetailUIState = stockDetailUIState,
                onUpButtonPressed = onUpButtonPressed,
                onTrackButtonPressed = onTrackButtonPressed
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChartSection(
                chartUIState = stockDetailUIState.value.chartUIState,
                chartRange = stockDetailUIState.value.chartRange,
                onChartRangeSelected = onChartRangeSelected
            )
            Spacer(modifier = Modifier.height(20.dp))
            CompanyInfoSection(stockDetailUIState.value.companyInfoUIState)
        }
    }
}

@Composable
fun StockDetailTopBar(
    stockDetailUIState: State<StockDetailUIState>,
    onUpButtonPressed: () -> Unit,
    onTrackButtonPressed: () -> Unit
) {
    Surface(
        elevation = AppBarDefaults.TopAppBarElevation,
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppBarDefaults.ContentPadding)
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { onUpButtonPressed() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.back)
                )
            }
            Text(
                text = stockDetailUIState.value.symbol,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { onTrackButtonPressed() }
            ) {
                Icon(
                    painter = painterResource(
                        if (stockDetailUIState.value.isTracked) {
                            R.drawable.ic_tracked
                        } else {
                            R.drawable.ic_not_tracked
                        }
                    ),
                    contentDescription = stringResource(
                        if (stockDetailUIState.value.isTracked) {
                            R.string.tracked
                        } else {
                            R.string.not_tracked
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun ChartSection(
    chartUIState: StockDetailUIState.ChartUIState,
    chartRange: ChartRange?,
    onChartRangeSelected: (ChartRange) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when (chartUIState) {
            is StockDetailUIState.ChartUIState.Working -> Box(contentAlignment = Alignment.Center) {
                if (chartUIState.loading) {
                    LoadingIndicator()
                }
                LineChart(
                    lineChartData = chartUIState.chartData,
                    linePathCalculator = BezierLinePathCalculator(),
                    lineDrawer = SolidLineDrawer(),
                    xAxisDrawer = NoXAxisDrawer,
                    yAxisDrawer = NoYAxisDrawer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                )
            }
            is StockDetailUIState.ChartUIState.Error -> {
                Text("ERROR: ${chartUIState.message}")
            }
        }
        chartRange?.let {
            CustomChartRangeSelector(
                currentRange = it,
                onRangeSelected = onChartRangeSelected,
                ranges = ChartRange.values().toList(),
            )
        }
    }
}

@Composable
fun CompanyInfoSection(
    companyInfoUIState: StockDetailUIState.CompanyInfoUIState
) {
    // Image(
    //     painter = rememberImagePainter(
    //         data = "https://storage.googleapis.com/iexcloud-hl37opg/api/logos/${quote.symbol}.png",
    //         builder = {
    //             crossfade(true)
    //         }
    //     ),
    //     contentDescription = "${quote.symbol} logo",
    //     modifier = Modifier
    //         .size(48.dp)
    //         .clip(MaterialTheme.shapes.medium)
    // )
    Text(
        text = when (companyInfoUIState) {
            is StockDetailUIState.CompanyInfoUIState.Loading -> "LOADING..."
            is StockDetailUIState.CompanyInfoUIState.Success -> "SUCCESS: ${companyInfoUIState.companyInfo.companyName}"
            is StockDetailUIState.CompanyInfoUIState.Error -> "ERROR: ${companyInfoUIState.message}"
        },
        style = MaterialTheme.typography.body1,
        textAlign = TextAlign.Center
    )
}

@Composable
fun stockDetailViewModel(symbol: String): StockDetailViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).stockDetailViewModelFactory()

    return viewModel(factory = StockDetailViewModel.provideFactory(factory, symbol))
}
