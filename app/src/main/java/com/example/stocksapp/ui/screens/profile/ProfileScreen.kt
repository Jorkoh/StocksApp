package com.example.stocksapp.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stocksapp.R
import com.example.stocksapp.data.datastore.SettingsDataStore.Companion.isDarkModeDefault
import com.example.stocksapp.data.datastore.SettingsDataStore.Companion.onlySearchStocksDefault
import com.example.stocksapp.data.datastore.SettingsDataStore.Companion.showRelevantNewsDefault
import com.example.stocksapp.ui.components.SectionTitle
import com.example.stocksapp.ui.components.SettingsItem
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    ProfileContent(
        isDarkMode = viewModel.isDarkMode.collectAsState(initial = isDarkModeDefault),
        onChangeIsDarkMode = viewModel::setIsDarkMode,
        showRelevantNews = viewModel.showRelevantNews.collectAsState(initial = showRelevantNewsDefault),
        onChangeShowRelevantNews = viewModel::setShowRelevantNews,
        onlySearchStocks = viewModel.onlySearchStocks.collectAsState(initial = onlySearchStocksDefault),
        onChangeOnlySearchStocks = viewModel::setOnlySearchStocks
    )
}

@Composable
fun ProfileContent(
    isDarkMode: State<Boolean>,
    onChangeIsDarkMode: (Boolean) -> Unit,
    showRelevantNews: State<Boolean>,
    onChangeShowRelevantNews: (Boolean) -> Unit,
    onlySearchStocks: State<Boolean>,
    onChangeOnlySearchStocks: (Boolean) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Spacer(modifier = Modifier.statusBarsPadding()) }
        item { SectionTitle(stringResource(R.string.settings_section_title)) }
        item {
            SettingsItem(
                primaryText = stringResource(R.string.settings_dark_mode),
                secondaryText = stringResource(R.string.settings_dark_mode_explanation),
                control = { Switch(checked = isDarkMode.value, onCheckedChange = onChangeIsDarkMode) },
            )
        }
        item {
            SettingsItem(
                primaryText = stringResource(R.string.settings_show_relevant_news),
                secondaryText = stringResource(R.string.settings_show_relevant_news_explanation),
                control = { Switch(checked = showRelevantNews.value, onCheckedChange = onChangeShowRelevantNews) }
            )
        }
        item {
            SettingsItem(
                primaryText = stringResource(R.string.settings_only_search_stocks),
                secondaryText = stringResource(R.string.settings_only_search_stocks_explanation),
                control = { Switch(checked = onlySearchStocks.value, onCheckedChange = onChangeOnlySearchStocks) }
            )
        }
        item { Divider(Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) }
        item {
            val context = LocalContext.current
            SettingsItem(
                primaryText = stringResource(R.string.settings_view_source_code),
                secondaryText = stringResource(R.string.settings_view_source_code_explanation),
                control = { },
                modifier = Modifier.clickable {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Jorkoh/StocksApp"))
                    )
                }
            )
        }
    }
}
