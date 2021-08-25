package com.example.stocksapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stocksapp.ui.theme.StocksAppTheme

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.h4,
        modifier = modifier.padding(start = 24.dp, bottom = 4.dp, top = 24.dp)
    )
}

@Preview
@Composable
fun SectionTitlePreview() {
    StocksAppTheme {
        Surface {
            Box(modifier = Modifier.padding(8.dp)) {
                SectionTitle(text = "The section")
            }
        }
    }
}
