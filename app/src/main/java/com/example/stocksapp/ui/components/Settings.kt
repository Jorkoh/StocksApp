package com.example.stocksapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stocksapp.ui.theme.StocksAppTheme

@Composable
fun SettingsItem(
    primaryText: String,
    secondaryText: String,
    modifier: Modifier = Modifier,
    control: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f, true)
        ) {
            Text(
                text = primaryText,
                style = MaterialTheme.typography.h5
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = secondaryText,
                style = MaterialTheme.typography.subtitle2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        control()
    }
}

@Preview
@Composable
fun SettingsItemsPreview() {
    StocksAppTheme {
        Surface {
            LazyColumn {
                items(5) {
                    SettingsItem(
                        primaryText = "Setting title",
                        secondaryText = "This is a setting explanation",
                        control = { Switch(checked = true, onCheckedChange = { }) }
                    )
                }
            }
        }
    }
}