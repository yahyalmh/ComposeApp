package com.example.compose.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.compose.nav.AppNavigation

/**
 * @author yaya (@yahyalmh)
 * @since 29th October 2022
 */
@Composable
fun MainScreen(navController: NavHostController, viewModel: MainViewModel = hiltViewModel()) {
    val uiState = viewModel.state.value

    Column {
        NetStatusView(uiState.showOnlineView, uiState.showOfflineView)
        Scaffold(
            contentColor = MaterialTheme.colors.onBackground,
            bottomBar = {
                AnimatedVisibility(visible = uiState.isBottomBarVisible) {
                    AppBottomBar(
                        destinations = viewModel.bottomBarTabs,
                        onNavigateToDestination = { tab ->
                            viewModel.onEvent(MainUiEvent.ChangeTab(navController, tab))
                        },
                        currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                        modifier = Modifier.testTag("AppBottomBar")
                    )
                }
            }
        ) { padding ->
            SetupAppNavigation(navController, padding)
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun SetupAppNavigation(
    navHostController: NavHostController,
    padding: PaddingValues = PaddingValues.Absolute()
) {
    AppNavigation(
        navController = navHostController,
        modifier = Modifier
            .padding(padding)
            .consumedWindowInsets(padding)
    )
}

