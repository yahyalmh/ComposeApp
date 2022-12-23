package com.example.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.common.ThemeType
import com.example.compose.nav.AppNavHost
import com.example.main.theme.AppTheme

/**
 * @author yaya (@yahyalmh)
 * @since 29th October 2022
 */
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.value

    val darkTheme = shouldUseDarkTheme(viewModel.state.value)
    AppTheme(useDarkTheme = darkTheme) {
        ContentView(uiState, navController, viewModel.bottomBarTabs) { tab ->
            viewModel.onEvent(MainUiEvent.ChangeTab(navController, tab))
        }
    }
}

@Composable
private fun ContentView(
    uiState: MainUiState,
    navController: NavHostController,
    bottomBarTabs: List<BottomBarTab>,
    onNavigateToDestination: (BottomBarTab) -> Unit
) {
    Column {
        NetStatusView(uiState.showOnlineView, uiState.showOfflineView)
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentColor = MaterialTheme.colors.background,
            bottomBar = {
                AnimatedVisibility(visible = uiState.isBottomBarVisible) {
                    AppBottomBar(
                        destinations = bottomBarTabs,
                        onNavigateToDestination = onNavigateToDestination,
                        currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                        modifier = Modifier.testTag("AppBottomBar")
                    )
                }
            }
        ) { paddingValues ->
            SetupAppNavHost(navController, paddingValues)
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun SetupAppNavHost(
    navHostController: NavHostController,
    padding: PaddingValues = PaddingValues.Absolute()
) {
    AppNavHost(
        navController = navHostController,
        modifier = Modifier
            .padding(padding)
            .consumedWindowInsets(padding)
    )
}

@Composable
fun shouldUseDarkTheme(uiState: MainUiState): Boolean =
    when (uiState.themeType) {
        ThemeType.SYSTEM -> isSystemInDarkTheme()
        ThemeType.LIGHT -> false
        ThemeType.DARK -> true
        else -> isSystemInDarkTheme()
    }

