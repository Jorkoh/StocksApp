package com.example.stocksapp.ui.screens.profile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    ProfileContent(
        isDarkMode = viewModel.isDarkMode.collectAsState(initial = isSystemInDarkTheme()),
        onChangeIsDarkMode = viewModel::SetIsDarkMode
    )
}

@Composable
fun ProfileContent(
    isDarkMode: State<Boolean>,
    onChangeIsDarkMode: (Boolean) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Spacer(modifier = Modifier.statusBarsPadding()) }
        item {
            Switch(
                checked = isDarkMode.value,
                onCheckedChange = onChangeIsDarkMode
            )
        }
    }
}
