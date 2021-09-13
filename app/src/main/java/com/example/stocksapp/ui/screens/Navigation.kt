package com.example.stocksapp.ui.screens

import com.example.stocksapp.R

sealed class Screen(val route: String) {
    object Home : RootDestination(
        route = "home",
        title = R.string.destination_home,
        icon = R.drawable.ic_home
    )

    object Search : RootDestination(
        route = "search",
        title = R.string.destination_search,
        icon = R.drawable.ic_search
    )

    object News : RootDestination(
        route = "news",
        title = R.string.destination_news,
        icon = R.drawable.ic_news
    )

    object Profile : RootDestination(
        route = "profile",
        title = R.string.destination_profile,
        icon = R.drawable.ic_profile
    )

    object StockDetail : Screen("stockDetail/{symbol}") {
        const val argument = "symbol"
        fun buildRoute(symbol: String) = "stockDetail/$symbol"
    }

    companion object {
        val StartDestination : RootDestination = Home
        fun listRootDestinations() = listOf(Home, Search, News, Profile)
        fun listScreens() = listRootDestinations() + StockDetail
    }
}

abstract class RootDestination(val title: Int, val icon: Int, route: String) : Screen(route)
