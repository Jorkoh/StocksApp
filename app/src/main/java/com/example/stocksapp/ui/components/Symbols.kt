package com.example.stocksapp.ui.components

import androidx.compose.animation.core.SnapSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.transform.CircleCropTransformation
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.ui.components.charts.line.LineChart
import com.example.stocksapp.ui.components.charts.line.LineChartData
import com.example.stocksapp.ui.components.charts.line.renderer.line.SolidLineDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.path.BezierLinePathCalculator
import com.example.stocksapp.ui.components.charts.line.renderer.xaxis.NoXAxisDrawer
import com.example.stocksapp.ui.components.charts.line.renderer.yaxis.NoYAxisDrawer
import com.example.stocksapp.ui.theme.StocksAppTheme
import com.example.stocksapp.ui.theme.greenStock
import com.example.stocksapp.ui.theme.redStock
import dev.chrisbanes.accompanist.coil.CoilImage
import java.util.*
import kotlin.math.sign
import kotlin.random.Random

@Composable
fun QuoteListItem(
    quote: Quote,
    modifier: Modifier = Modifier,
    onSymbolSelected: (String) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clickable(onClick = { onSymbolSelected(quote.symbol) })
            .padding(horizontal = 24.dp, vertical = 14.dp)
            .height(48.dp),
        verticalAlignment = Alignment.Top
    ) {
        CoilImage(
            data = "https://storage.googleapis.com/iexcloud-hl37opg/api/logos/${quote.symbol}.png",
            contentDescription = "${quote.symbol} logo",
            requestBuilder = { transformations(CircleCropTransformation()) },
            modifier = Modifier.preferredSize(48.dp)
        )
        Column(
            modifier = Modifier.weight(1f, true).padding(start = 16.dp)
        ) {
            Text(
                text = quote.symbol,
                style = MaterialTheme.typography.h6
            )
            Providers(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = quote.companyName,
                    style = MaterialTheme.typography.caption.copy(fontSize = 13.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Column(
            modifier = Modifier.padding(start = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "%.2f".format(quote.latestPrice),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.End
            )
            val changeColor = when (quote.change.sign) {
                -1.0 -> redStock
                1.0 -> greenStock
                else -> LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
            }
            val backgroundModifier = Modifier.background(
                shape = MaterialTheme.shapes.small,
                color = changeColor.copy(alpha = 0.1f)
            )
            Box(modifier = backgroundModifier.padding(horizontal = 2.dp)) {
                Text(
                    text = "${"%+.2f".format(quote.changePercent * 100)}%",
                    style = MaterialTheme.typography.caption.copy(fontSize = 13.sp),
                    textAlign = TextAlign.End,
                    color = changeColor
                )
            }
        }
    }
}

@Composable
fun QuoteWithChartCard(
    symbol: String,
    chartData: LineChartData,
    modifier: Modifier = Modifier,
    onSymbolSelected: (String) -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = { onSymbolSelected(symbol) }).size(164.dp),
        elevation = 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier.height(20.dp))
            LineChart(
                lineChartData = chartData,
                linePathCalculator = BezierLinePathCalculator(),
                lineDrawer = SolidLineDrawer(
                    color = if (chartData.points.first().value > chartData.points.last().value) {
                        Color.Red
                    } else {
                        Color.Green
                    }
                ),
                xAxisDrawer = NoXAxisDrawer,
                yAxisDrawer = NoYAxisDrawer,
                animation = SnapSpec(0)
            )
        }
    }
}

@Preview
@Composable
fun QuoteListItemsPreview() {
    val quotes = List(5) {
        Quote(
            symbol = "AMD",
            companyName = "Advanced Micro Devices, Inc",
            primaryExchange = "",
            openPrice = 0.0,
            openTime = 0,
            closePrice = 0.0,
            closeTime = 0,
            highPrice = 0.0,
            highTime = 0,
            lowPrice = 0.0,
            lowTime = 0,
            latestPrice = 86.77,
            latestSource = "",
            latestTime = 0,
            latestVolume = 0,
            extendedPrice = 0.0,
            extendedChange = 0.0,
            extendedChangePercent = 0.0,
            extendedPriceTime = 0,
            previousClose = 0.0,
            previousVolume = 0,
            change = -2.84,
            changePercent = -0.0317,
            volume = 0,
            avgTotalVolume = 0,
            marketCap = 0,
            peRatio = 0.0,
            week52High = 0.0,
            week52Low = 0.0,
            ytdChange = 0.0,
            lastTradeTime = 0,
            isUSMarketOpen = true
        )
    }
    StocksAppTheme {
        Surface {
            LazyColumn {
                items(items = quotes) {
                    QuoteListItem(
                        quote = it,
                        onSymbolSelected = {}
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TickerCardPreview() {
    QuoteWithChartCard(
        symbol = "AMD",
        chartData = LineChartData(
            points = (1..7).map { LineChartData.Point(randomYValue(), "#$it") }
        ),
        onSymbolSelected = {}
    )
}

private fun randomYValue() = Random.nextDouble(5.0, 20.0).toFloat()