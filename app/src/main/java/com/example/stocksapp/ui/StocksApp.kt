package com.example.stocksapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastFirstOrNull
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.example.stocksapp.ui.components.CustomBottomBar
import com.example.stocksapp.ui.screens.NavigableDestinations
import com.example.stocksapp.ui.screens.NavigableScreens
import com.example.stocksapp.ui.screens.home.HomeScreen
import com.example.stocksapp.ui.screens.news.NewsScreen
import com.example.stocksapp.ui.screens.profile.ProfileScreen
import com.example.stocksapp.ui.screens.search.SearchScreen
import com.example.stocksapp.ui.screens.stockdetail.StockDetailScreen
import com.example.stocksapp.ui.screens.stockdetail.stockDetailViewModel
import com.example.stocksapp.ui.theme.StocksAppTheme
import com.example.stocksapp.ui.utils.LocalSysUiController
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

@Composable
fun StocksApp() {
    val navController = rememberNavController()

    LocalSysUiController.current.setSystemBarsColor(
        color = MaterialTheme.colors.surface.copy(alpha = 0.85f)
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
            HomeScreen(it.hiltNavGraphViewModel(), navController, padding)
        }
        composable(NavigableDestinations.Search.route) { SearchScreen(padding) }
        composable(NavigableDestinations.News.route) {
            NewsScreen(it.hiltNavGraphViewModel(), navController, padding)
        }
        composable(NavigableDestinations.Profile.route) { ProfileScreen(padding) }

        // Deeper screens, don't use padding because the bottom bar won't be present
        composable(
            route = NavigableScreens.StockDetail.routeWithArgument,
            arguments = listOf(
                navArgument(NavigableScreens.StockDetail.argument) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val symbol = requireNotNull(
                backStackEntry.arguments?.getString(NavigableScreens.StockDetail.argument)
            )
            StockDetailScreen(stockDetailViewModel(symbol), navController)
        }
    }
}

@Composable
private fun NavigableBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var lastNavigableDestination by remember {
        mutableStateOf<NavigableDestinations>(NavigableDestinations.StartDestination)
    }

    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE) ?: return
    val currentNavigableDestination = NavigableDestinations.toList().fastFirstOrNull {
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
            currentDestination = lastNavigableDestination.destination,
            onDestinationSelected = { newDestination ->
                if (lastNavigableDestination.destination != newDestination) {
                    val newRoute = NavigableDestinations.toList().first {
                        it.destination == newDestination
                    }.route
                    navController.navigate(newRoute) {
                        popUpTo = navController.graph.startDestination
                        launchSingleTop = true
                    }
                }
            },
            destinations = NavigableDestinations.toList().map { it.destination },
        )
    }
}

// https://kotlinlang.slack.com/archives/CJLTWPH7S/p1604071670473700?thread_ts=1604043017.440100&cid=CJLTWPH7S
@Composable
inline fun <reified VM : ViewModel> NavBackStackEntry.hiltNavGraphViewModel(): VM {
    val viewModelFactory = HiltViewModelFactory(LocalContext.current, this)
    return ViewModelProvider(this, viewModelFactory).get(VM::class.java)
}
