package com.example.stocksapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.stocksapp.data.datastore.SettingsDataStore
import com.example.stocksapp.ui.StocksApp
import com.example.stocksapp.ui.screens.stockdetail.StockDetailViewModelFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settings: SettingsDataStore

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun stockDetailViewModelFactory(): StockDetailViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manually handle insets
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            StocksApp(settings)
        }
    }
}
