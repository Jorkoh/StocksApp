package com.example.stocksapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.stocksapp.ui.AmbientSystemUiController
import com.example.stocksapp.ui.StocksApp
import com.example.stocksapp.ui.SystemUiController
import com.example.stocksapp.ui.theme.StocksAppTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            Providers(AmbientSystemUiController provides systemUiController) {
                StocksApp()
            }
        }
    }
}