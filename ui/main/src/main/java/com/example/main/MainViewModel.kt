package com.example.main

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions
import com.example.common.SharedState
import com.example.common.ThemeType
import com.example.common.toThemeType
import com.example.common.BaseViewModel
import com.example.common.UIEvent
import com.example.common.UIState
import com.example.main.connectivity.ConnectivityMonitor
import com.example.datastore.DatastoreInteractor
import com.example.favorite.nav.navigateToFavorite
import com.example.home.nav.navigateToHome
import com.example.setting.nav.navigateToSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkMonitor: ConnectivityMonitor,
    private val datastoreInteractor: DatastoreInteractor,
) : BaseViewModel<MainUiState, MainUiEvent>(MainUiState.None) {
    private var isAppLaunchedForFirstTime: Boolean = true
    val bottomBarTabs: List<BottomBarTab> = BottomBarTab.values().asList()

    init {
        observeBottomBarState()
        observeNetworkState()
        observeThemeChange()
    }

    private fun observeBottomBarState() {
        SharedState.bottomBarVisible.onEach {
            setState(
                MainUiState.BottomVisibleChange(
                    state.value.showOnlineView,
                    state.value.showOfflineView,
                    isBottomBarVisible = it,
                    themeType = state.value.themeType ?: ThemeType.SYSTEM
                )
            )
        }.launchIn(viewModelScope)
    }

    private fun observeNetworkState() {
        networkMonitor.isOnline
            .distinctUntilChanged()
            .onEach { isOnline ->
                if (isOnline) {
                    handelOnlineState()
                } else {
                    setState(MainUiState.Offline)
                }
            }.launchIn(viewModelScope)
    }

    private fun observeThemeChange() {
        datastoreInteractor
            .getThemeType()
            .onEach {
                val themeType = it?.toThemeType() ?: ThemeType.SYSTEM
                setState(
                    MainUiState.ChangeTheme(
                        state.value.showOnlineView,
                        state.value.showOfflineView,
                        themeType
                    )
                )
            }
            .launchIn(viewModelScope)

    }

    private fun handelOnlineState() {
        if (isAppLaunchedForFirstTime) {
            isAppLaunchedForFirstTime = false
            return
        }
        setState(MainUiState.Online)
        hideOnlineViewAfterWhile()
    }

    private fun hideOnlineViewAfterWhile() {
        val hideOnlineViewDelay: Long = 2000
        viewModelScope.launch {
            delay(hideOnlineViewDelay)
            setState(MainUiState.None)
        }
    }

    override fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.ChangeTab -> changeBottomBarDestination(
                event.navController,
                destination = event.destination
            )
        }
    }

    private fun changeBottomBarDestination(
        navController: NavController,
        destination: BottomBarTab
    ) {
        val id = navController.graph.findStartDestination().id
        val navOptions = navOptions {
            popUpTo(id) {
//                saveState = true
            }
            launchSingleTop = true
//            restoreState = true
        }

        when (destination) {
            BottomBarTab.Home -> navController.navigateToHome(navOptions)
            BottomBarTab.FAVORITE -> navController.navigateToFavorite(navOptions)
            BottomBarTab.SETTING -> navController.navigateToSetting(navOptions)
        }
    }
}

sealed interface MainUiEvent : UIEvent {
    class ChangeTab(val navController: NavController, val destination: BottomBarTab) : MainUiEvent
}

sealed class MainUiState(
    val showOnlineView: Boolean,
    val showOfflineView: Boolean,
    val themeType: ThemeType? = null,
    val isBottomBarVisible: Boolean = true,
) : UIState {

    object None : MainUiState(false, false)

    object Offline : MainUiState(
        showOnlineView = false,
        showOfflineView = true,
    )

    object Online : MainUiState(
        showOnlineView = true,
        showOfflineView = false,
    )

    class BottomVisibleChange(
        showOnlineView: Boolean,
        showOfflineView: Boolean,
        isBottomBarVisible: Boolean,
        themeType: ThemeType
    ) : MainUiState(
        showOnlineView = showOnlineView,
        showOfflineView = showOfflineView,
        isBottomBarVisible = isBottomBarVisible,
        themeType = themeType
    )

    class ChangeTheme(
        showOnlineView: Boolean,
        showOfflineView: Boolean,
        themeType: ThemeType
    ) : MainUiState(
        showOnlineView = showOnlineView,
        showOfflineView = showOfflineView,
        themeType = themeType
    )
}

