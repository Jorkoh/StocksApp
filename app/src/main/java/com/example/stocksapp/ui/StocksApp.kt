package com.example.stocksapp.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.*
import com.example.stocksapp.ui.components.CustomBottomBar
import com.example.stocksapp.ui.screens.CartScreen
import com.example.stocksapp.ui.screens.HomeScreen
import com.example.stocksapp.ui.screens.ProfileScreen
import com.example.stocksapp.ui.screens.SearchScreen
import com.example.stocksapp.ui.theme.StocksAppTheme
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.statusBarsPadding

@Composable
fun StocksApp() {
    val navController = rememberNavController()
    val sysUiController = AmbientSystemUiController.current

    sysUiController.setSystemBarsColor(
        color = MaterialTheme.colors.surface.copy(alpha = 0.8f)
    )

    ProvideWindowInsets {
        StocksAppTheme {
            Scaffold(
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
                    val currentDestination = NavigableDestination.values()
                        .firstOrNull { it.route == currentRoute }
                        ?: NavigableDestination.StartDestination

                    CustomBottomBar(
                        currentDestination = currentDestination.destination,
                        onDestinationSelected = { destination ->
                            val route = NavigableDestination.values()
                                .first { it.destination == destination }.route
                            navController.navigate(route) {
                                popUpTo = navController.graph.startDestination
                                launchSingleTop = true
                            }
                        },
                        destinations = NavigableDestination.values().map { it.destination },
                    )
                }
            ) { innerPadding ->
                val modifier = Modifier.padding(innerPadding)

                NavHost(navController, NavigableDestination.StartDestination.route) {
                    composable(NavigableDestination.Home.route) { HomeScreen(modifier) }
                    composable(NavigableDestination.Search.route) { SearchScreen(modifier) }
                    composable(NavigableDestination.Cart.route) { CartScreen(modifier) }
                    composable(NavigableDestination.Profile.route) { ProfileScreen(modifier) }
                }
            }
        }
    }
}