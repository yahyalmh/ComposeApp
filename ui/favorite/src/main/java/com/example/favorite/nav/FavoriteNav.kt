package com.example.favorite.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.favorite.FavoriteScreen

const val favoriteRoute = "favorite_route"

fun NavController.navigateToFavorite(navOptions: NavOptions? = null) {
    this.navigate(favoriteRoute, navOptions)
}

fun NavGraphBuilder.favoriteGraph(navController: NavHostController) {
    composable(route = favoriteRoute) {
        FavoriteScreen(navController = navController)
    }
}