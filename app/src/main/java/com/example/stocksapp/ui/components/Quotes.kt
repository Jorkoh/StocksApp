package com.example.stocksapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.example.stocksapp.data.model.Quote
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlin.math.absoluteValue

@Composable
fun QuoteListItem(
    quote: Quote,
    onSymbolSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = { onSymbolSelected(quote.symbol) })
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoilImage(
            data = "https://storage.googleapis.com/iexcloud-hl37opg/api/logos/${quote.symbol}.png",
            contentDescription = "${quote.symbol} logo",
            requestBuilder = { transformations(CircleCropTransformation()) },
            modifier = Modifier.preferredSize(48.dp)
        )
        Spacer(modifier = Modifier.preferredWidth(16.dp).fillMaxHeight())
        Column(
            modifier = Modifier.weight(1f, true).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = quote.symbol,
                style = MaterialTheme.typography.h6
            )
            Providers(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = quote.companyName,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.preferredWidth(16.dp).fillMaxHeight())
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${"%.2f".format(quote.changePercent * 100)}%",
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.End
            )
            Providers(LocalContentAlpha provides ContentAlpha.medium) {
                val sign = if (quote.change < 0) "-" else ""
                Text(
                    text = "$sign$${"%.2f".format(quote.change.absoluteValue)}",
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}