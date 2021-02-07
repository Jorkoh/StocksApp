package com.example.stocksapp.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.stocksapp.R

data class Destination(
    @StringRes val title: Int,
    val icon: ImageVector
)

enum class NavigableDestination(val route: String, val destination: Destination) {
    Home(
        route = "home",
        destination = Destination(R.string.destination_home, Icons.Outlined.Home)
    ),
    Search(
        route = "search",
        destination = Destination(R.string.destination_search, Icons.Outlined.Search)
    ),
    Cart(
        route = "cart",
        destination = Destination(R.string.destination_cart, Icons.Outlined.ShoppingCart)
    ),
    Profile(
        route = "profile",
        destination = Destination(R.string.destination_profile, Icons.Outlined.AccountCircle)
    );

    companion object {
        val StartDestination = Home
    }
}

val previewDestinations = listOf(
    Destination(R.string.destination_home, Icons.Outlined.Home),
    Destination(R.string.destination_search, Icons.Outlined.Search),
    Destination(R.string.destination_cart, Icons.Outlined.ShoppingCart),
    Destination(R.string.destination_profile, Icons.Outlined.AccountCircle),
)