package com.example.stocksapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.transform.CircleCropTransformation
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.ui.theme.greenStock
import com.example.stocksapp.ui.theme.redStock
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlin.math.sign

@Composable
fun QuoteListItem(
    quote: Quote,
    onSymbolSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
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
                    style = MaterialTheme.typography.caption,
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
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.End,
                    color = changeColor
                )
            }
        }
    }
}