package com.example.stocksapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastFirstOrNull
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stocksapp.ui.components.CustomBottomBar
import com.example.stocksapp.ui.screens.RootDestination
import com.example.stocksapp.ui.screens.Screen
import com.example.stocksapp.ui.screens.home.HomeScreen
import com.example.stocksapp.ui.screens.news.NewsScreen
import com.example.stocksapp.ui.screens.profile.ProfileScreen
import com.example.stocksapp.ui.screens.search.SearchScreen
import com.example.stocksapp.ui.screens.stockdetail.StockDetailScreen
import com.example.stocksapp.ui.screens.stockdetail.stockDetailViewModel
import com.example.stocksapp.ui.theme.StocksAppTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun StocksApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val systemUiController = rememberSystemUiController()

    ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
        StocksAppTheme {
            val colors = MaterialTheme.colors
            val currentScreen = Screen.listScreens().fastFirstOrNull {
                it.route == navBackStackEntry?.destination?.route
            }

            val color by animateColorAsState(
                targetValue = when (currentScreen) {
                    is RootDestination -> colors.background.copy(alpha = 0.85f)
                    !is RootDestination -> colors.primary
                    else -> colors.background.copy(alpha = 0.85f)
                },
                animationSpec = spring(Spring.DampingRatioNoBouncy, 800F)
            )

            LaunchedEffect(color) {
                systemUiController.setSystemBarsColor(color = color, darkIcons = colors.isLight)
            }

            Scaffold(
                bottomBar = { NavigableBottomBar(navController) },
                content = { innerPadding -> NavigableContent(innerPadding, navController) },
            )
        }
    }
}

@Composable
private fun NavigableContent(
    innerPadding: PaddingValues,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.StartDestination.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        // Base destinations
        composable(Screen.Home.route) {
            HomeScreen(hiltViewModel(it), navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(hiltViewModel(it), navController)
        }
        composable(Screen.News.route) {
            NewsScreen(hiltViewModel(it), navController)
        }
        composable(Screen.Profile.route) { ProfileScreen() }

        // Deeper screens
        composable(Screen.StockDetail.route) { backStackEntry ->
            val symbol = requireNotNull(
                backStackEntry.arguments?.getString(Screen.StockDetail.argument)
            )
            StockDetailScreen(stockDetailViewModel(symbol), navController)
        }
    }
}

@Composable
private fun NavigableBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var lastNavigableDestination by remember { mutableStateOf(Screen.StartDestination) }

    val currentRoute = navBackStackEntry?.destination?.route ?: return
    val currentNavigableDestination = Screen.listRootDestinations().fastFirstOrNull {
        it.route == currentRoute
    }?.let { currentNavigableDestination ->
        lastNavigableDestination = currentNavigableDestination
    }

    AnimatedVisibility(
        visible = currentNavigableDestination != null,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 100, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 125, easing = FastOutLinearInEasing)
        )
    ) {
        CustomBottomBar(
            currentDestination = lastNavigableDestination,
            onDestinationSelected = { newDestination ->
                if (lastNavigableDestination != newDestination) {
                    navController.navigate(newDestination.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            destinations = Screen.listRootDestinations(),
            modifier = Modifier.navigationBarsWithImePadding()
        )
    }
}
