package com.example.stocksapp.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.stocksapp.R

data class Destination(
    @StringRes val title: Int,
    @DrawableRes val icon: Int
)

enum class NavigableDestination(val route: String, val destination: Destination) {
    Home(
        route = "home",
        destination = Destination(R.string.destination_home, R.drawable.ic_home)
    ),
    Search(
        route = "search",
        destination = Destination(R.string.destination_search, R.drawable.ic_search)
    ),
    Cart(
        route = "news",
        destination = Destination(R.string.destination_news, R.drawable.ic_news)
    ),
    Profile(
        route = "profile",
        destination = Destination(R.string.destination_profile, R.drawable.ic_profile)
    );

    companion object {
        val StartDestination = Home
    }
}

val previewDestinations = listOf(
    Destination(R.string.destination_home, R.drawable.ic_home),
    Destination(R.string.destination_search, R.drawable.ic_search),
    Destination(R.string.destination_news, R.drawable.ic_news),
    Destination(R.string.destination_profile, R.drawable.ic_profile),
)