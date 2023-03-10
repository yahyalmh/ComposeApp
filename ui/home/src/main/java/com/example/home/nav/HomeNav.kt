package com.example.home.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.home.HomeScreen

/**
 * @author yaya (@yahyalmh)
 * @since 05th November 2022
 */


const val homeRoute = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeRoute, navOptions)
}

fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    composable(route = homeRoute) {
        HomeScreen(navController = navController)
    }
}