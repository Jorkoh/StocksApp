package com.example.stocksapp.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.stocksapp.R


sealed class NavigableScreens(val route: String) {

    // TODO this is kinda scuffed tbh
    object StockDetail : NavigableScreens("StockDetail") {
        const val argument = "symbol"
        const val routeWithArgument = "StockDetail/{$argument}"
        fun buildRoute(symbol: String) = "StockDetail/$symbol"
    }
}

sealed class NavigableDestinations(
    route: String,
    val destination: Destination
) : NavigableScreens(route) {

    object Home : NavigableDestinations(
        route = "home",
        destination = Destination(R.string.destination_home, R.drawable.ic_home)
    )

    object Search : NavigableDestinations(
        route = "search",
        destination = Destination(R.string.destination_search, R.drawable.ic_search)
    )

    object News : NavigableDestinations(
        route = "news",
        destination = Destination(R.string.destination_news, R.drawable.ic_news)
    )

    object Profile : NavigableDestinations(
        route = "profile",
        destination = Destination(R.string.destination_profile, R.drawable.ic_profile)
    )

    companion object {
        val StartDestination = Home
        fun toList() = listOf(Home, Search, News, Profile)
    }
}

data class Destination(
    @StringRes val title: Int,
    @DrawableRes val icon: Int
)

