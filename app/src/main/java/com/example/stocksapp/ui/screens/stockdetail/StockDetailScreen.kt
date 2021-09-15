package com.example.stocksapp.ui.screens.stockdetail

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.stocksapp.MainActivity
import com.example.stocksapp.R
import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.data.repositories.stocks.ChartRange
import com.example.stocksapp.ui.components.CustomChartRangeSelector
import com.example.stocksapp.ui.components.LoadingIndicator
import com.example.stocksapp.ui.components.charts.line.LineChart
import com.example.stocksapp.ui.components.charts.line.renderer.line.SolidLineDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.path.BezierLinePathCalculator
import com.example.stocksapp.ui.components.charts.line.renderer.xaxis.NoXAxisDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.yaxis.NoYAxisDrawer
import com.example.stocksapp.ui.theme.loss
import com.example.stocksapp.ui.theme.profit
import com.google.accompanist.insets.statusBarsPadding
import dagger.hilt.android.EntryPointAccessors
import kotlin.math.sign

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
                symbol = stockDetailUIState.value.symbol,
                isTracked = stockDetailUIState.value.isTracked,
                onUpButtonPressed = onUpButtonPressed,
                onTrackButtonPressed = onTrackButtonPressed
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                PriceSection(
                    chartUIState = stockDetailUIState.value.chartUIState,
                    nonChartUIState = stockDetailUIState.value.nonChartUIState,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            item {
                ChartSection(
                    chartUIState = stockDetailUIState.value.chartUIState,
                    onChartRangeSelected = onChartRangeSelected,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            item {
                CompanyInfoSection(
                    symbol = stockDetailUIState.value.symbol,
                    nonChartUIState = stockDetailUIState.value.nonChartUIState
                )
            }
        }
    }
}

@Composable
fun StockDetailTopBar(
    symbol: String,
    isTracked: Boolean,
    onUpButtonPressed: () -> Unit,
    onTrackButtonPressed: () -> Unit,
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
                text = symbol,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { onTrackButtonPressed() }
            ) {
                Icon(
                    painter = painterResource(if (isTracked) R.drawable.ic_tracked else R.drawable.ic_not_tracked),
                    contentDescription = stringResource(if (isTracked) R.string.tracked else R.string.not_tracked)
                )
            }
        }
    }
}

@Composable
fun PriceSection(
    chartUIState: StockDetailUIState.ChartUIState,
    nonChartUIState: StockDetailUIState.NonChartUIState,
    modifier: Modifier = Modifier
) {
    val price = (nonChartUIState as? StockDetailUIState.NonChartUIState.Success)?.quote?.latestPrice
    val priceString = if (price != null) "%.2f".format(price) else "-"

    val points = (chartUIState as? StockDetailUIState.ChartUIState.Working)?.chartData?.points
    val change = points?.let { (it.last().value - it.first().value) / it.first().value }?.toDouble() ?: 0.0

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = priceString,
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.End
        )
        Spacer(Modifier.height(4.dp))

        val changeColor = when (change.sign) {
            -1.0 -> MaterialTheme.colors.loss
            1.0 -> MaterialTheme.colors.profit
            else -> LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
        }
        val backgroundModifier = Modifier.background(
            shape = MaterialTheme.shapes.small,
            color = changeColor.copy(alpha = 0.1f)
        )
        Box(modifier = backgroundModifier.padding(horizontal = 3.dp)) {
            Text(
                text = "${"%+.2f".format(change * 100)}%",
                style = MaterialTheme.typography.subtitle1.copy(fontSize = 14.sp),
                textAlign = TextAlign.End,
                color = changeColor
            )
        }
    }
}

@Composable
fun ChartSection(
    chartUIState: StockDetailUIState.ChartUIState,
    onChartRangeSelected: (ChartRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
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
                        .aspectRatio(1.5f, matchHeightConstraintsFirst = true)
                )
            }
            is StockDetailUIState.ChartUIState.Error -> {
                Text("ERROR: ${chartUIState.message}")
            }
        }
        chartUIState.chartRange?.let {
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
    symbol: String,
    nonChartUIState: StockDetailUIState.NonChartUIState
) {
    when (nonChartUIState) {
        is StockDetailUIState.NonChartUIState.Loading -> Text(text = "LOADING...")
        is StockDetailUIState.NonChartUIState.Success -> {
            CompanyInfo(
                symbol = symbol,
                companyInfo = nonChartUIState.companyInfo,
                quote = nonChartUIState.quote
            )
        }
        is StockDetailUIState.NonChartUIState.Error -> Text(text = "ERROR: ${nonChartUIState.message}")
    }
}

@Composable
private fun CompanyInfo(symbol: String, companyInfo: CompanyInfo, quote: Quote) {
    Card(
        modifier = Modifier.padding(horizontal = 12.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .fillMaxWidth(),
        ) {
            CompanyNameAndLogo(symbol, companyInfo.companyName)
            Spacer(modifier = Modifier.height(24.dp))
            InfoItem(label = "CEO", content = companyInfo.CEO)
            InfoDivider()
            InfoItem(label = "Country", content = companyInfo.country)
            InfoDivider()
            InfoItem(label = "Industry", content = companyInfo.industry)
            InfoDivider()
            InfoItem(label = "Sector", content = companyInfo.sector)
            InfoDivider()
            InfoItem(label = "Employees", content = companyInfo.employees?.toString())
            InfoDivider()
            InfoItem(label = "P/E Ratio", content = quote.peRatio?.let { "%.2f".format(it) })
            InfoDivider()
            InfoItem(label = "Market Cap", content = quote.marketCap?.toString())
            InfoDivider()
            InfoItem(label = "Volume", content = quote.volume?.toString())
        }
    }
}

@Composable
private fun CompanyNameAndLogo(symbol: String, companyName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = rememberImagePainter(
                data = "https://storage.googleapis.com/iexcloud-hl37opg/api/logos/$symbol.png",
                builder = {
                    crossfade(true)
                }
            ),
            contentDescription = "$symbol logo",
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Text(
            text = companyName,
            style = MaterialTheme.typography.body1.copy(fontSize = 18.sp),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun InfoDivider() {
    Divider(modifier = Modifier.padding(vertical = 12.dp))
}

@Composable
private fun InfoItem(label: String, content: String?) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = if (!content.isNullOrBlank()) content else "-",
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
