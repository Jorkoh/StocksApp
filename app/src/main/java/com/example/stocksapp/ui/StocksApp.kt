package com.example.stocksapp.ui

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.stocksapp.ui.components.CustomBottomBar
import com.example.stocksapp.ui.screens.NavigableDestinations
import com.example.stocksapp.ui.screens.NavigableScreens
import com.example.stocksapp.ui.screens.home.HomeScreen
import com.example.stocksapp.ui.screens.news.NewsScreen
import com.example.stocksapp.ui.screens.profile.ProfileScreen
import com.example.stocksapp.ui.screens.search.SearchScreen
import com.example.stocksapp.ui.screens.stockdetail.StockDetailScreen
import com.example.stocksapp.ui.theme.StocksAppTheme
import com.example.stocksapp.ui.utils.LocalSysUiController
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import timber.log.Timber

@Composable
fun StocksApp() {
    val navController = rememberNavController()

    LocalSysUiController.current.setSystemBarsColor(
        color = MaterialTheme.colors.surface.copy(alpha = 0.8f)
    )

    ProvideWindowInsets {
        StocksAppTheme {
            Scaffold(
                bottomBar = { NavigableBottomBar(navController) }
            ) { innerPadding ->
                NavigableContent(innerPadding, navController)
            }
        }
    }
}

@Composable
private fun NavigableContent(
    innerPadding: PaddingValues,
    navController: NavHostController
) {
    val padding = Modifier.padding(innerPadding)

    NavHost(navController, NavigableDestinations.StartDestination.route) {
        // Base destinations
        composable(NavigableDestinations.Home.route) {
            HomeScreen(padding) { symbol ->
                navController.navigate(NavigableScreens.StockDetail.buildRoute(symbol))
            }
        }
        composable(NavigableDestinations.Search.route) { SearchScreen(padding) }
        composable(NavigableDestinations.News.route) { NewsScreen(padding) }
        composable(NavigableDestinations.Profile.route) { ProfileScreen(padding) }

        // Deeper screens, don't use padding because the bottom bar won't be present
        composable(
            route = NavigableScreens.StockDetail.routeWithArgument,
            arguments = listOf(navArgument(NavigableScreens.StockDetail.argument) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val symbol = requireNotNull(
                backStackEntry.arguments?.getString(NavigableScreens.StockDetail.argument)
            )
            // TODO get this symbol to the view model
            StockDetailScreen()
        }
    }
}

@Composable
private fun NavigableBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
        ?: NavigableDestinations.StartDestination.route

    // If the current route is not part of the bottom bar don't compose
    if (!NavigableDestinations.toList().map { it.route }.contains(currentRoute)) return

    val currentDestination = NavigableDestinations.toList().first { it.route == currentRoute }

    CustomBottomBar(
        currentDestination = currentDestination.destination,
        onDestinationSelected = { destination ->
            val route = NavigableDestinations.toList()
                .first { it.destination == destination }.route
            navController.navigate(route) {
                popUpTo = navController.graph.startDestination
                launchSingleTop = true
            }
        },
        destinations = NavigableDestinations.toList().map { it.destination },
    )
}