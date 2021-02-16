package com.example.stocksapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.example.stocksapp.ui.StocksApp
import com.example.stocksapp.ui.utils.LocalSysUiController
import com.example.stocksapp.ui.utils.SystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manually handle insets
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            Providers(LocalSysUiController provides systemUiController) {
                StocksApp()
            }
        }
    }
}