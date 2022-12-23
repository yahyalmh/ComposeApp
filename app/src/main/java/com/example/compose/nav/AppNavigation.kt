package com.example.compose.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.favorite.nav.favoriteGraph
import com.example.setting.nav.settingGraph
import com.example.detail.nav.detailGraph
import com.example.home.nav.homeGraph
import com.example.home.nav.homeRoute
import com.example.search.nav.searchGraph

/**
 * @author yaya (@yahyalmh)
 * @since 29th October 2022
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = homeRoute
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeGraph(navController)
        detailGraph(navController)
        searchGraph(navController)
        settingGraph(navController)
        favoriteGraph(navController)
    }
}