package com.example.stocksapp.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.stocksapp.R

sealed class NavigableScreen(val route: String) {

    object StockDetail : NavigableScreen("StockDetail") {
        const val argument = "symbol"
        const val routeWithArgument = "stockDetail/{$argument}"
        fun buildRoute(symbol: String) = "stockDetail/$symbol"
    }
}

sealed class NavigableDestination(
    route: String,
    val destination: Destination
) : NavigableScreen(route) {

    object Home : NavigableDestination(
        route = "home",
        destination = Destination(R.string.destination_home, R.drawable.ic_home)
    )

    object Search : NavigableDestination(
        route = "search",
        destination = Destination(R.string.destination_search, R.drawable.ic_search)
    )

    object News : NavigableDestination(
        route = "news",
        destination = Destination(R.string.destination_news, R.drawable.ic_news)
    )

    object Profile : NavigableDestination(
        route = "profile",
        destination = Destination(R.string.destination_profile, R.drawable.ic_profile)
    )

    companion object {
        val StartDestination = Home
        fun listAll() = listOf(Home, Search, News, Profile)
    }
}

data class Destination(
    @StringRes val title: Int,
    @DrawableRes val icon: Int
)
