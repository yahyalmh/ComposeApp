package com.example.detail.nav

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.example.detail.DetailScreen

const val detailRoute = "detail_route"

@VisibleForTesting
internal const val rateIdArgKey = "rateId"

internal class DetailArgs(savedStateHandle: SavedStateHandle) {
    var rateId: String = savedStateHandle.get<String>(rateIdArgKey).toString()
}

fun NavController.navigateToDetail(rateId: String, navOptions: NavOptions? = null) {
    this.navigate("$detailRoute/$rateId", navOptions)
}

fun NavGraphBuilder.detailGraph(navController: NavHostController) {
    composable(
        route = "$detailRoute/{$rateIdArgKey}",
        arguments = listOf(navArgument(rateIdArgKey) { type = NavType.StringType })
    ) {
        DetailScreen(navController = navController)
    }
}