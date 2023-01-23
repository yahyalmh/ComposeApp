package com.example.home

import androidx.lifecycle.viewModelScope
import com.example.data.common.model.ExchangeRate
import com.example.favorite.FavoriteRatesInteractor
import com.example.home.util.Constant
import com.example.rate.ExchangeRateInteractor
import com.example.ui.common.BaseViewModel
import com.example.ui.common.UIEvent
import com.example.ui.common.UIState
import com.example.ui.common.connectivity.ConnectivityMonitor
import com.example.ui.common.ext.retryOnNetworkConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author yaya (@yahyalmh)
 * @since 05th November 2022
 */

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val exchangeRateInteractor: ExchangeRateInteractor,
    private val favoriteRatesInteractor: FavoriteRatesInteractor,
    private val connectivityMonitor: ConnectivityMonitor,
) : BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState.Loading) {

    init {
        fetchRates()
    }

    private fun fetchRates() {
        combine(
            exchangeRateInteractor.getLiveRates(Constant.liveRateFetchInterval),
            favoriteRatesInteractor.getFavoriteRates()
        ) { rates, favoriteRates ->
            setState(HomeUiState.Loaded(rates = rates, favoriteRates = favoriteRates))
        }
            .retryOnNetworkConnection(connectivityMonitor) { e -> handleAutoRetry(e) }
            .catch { e -> handleRetry(e) }
            .launchIn(viewModelScope)
    }

    private fun handleFavorite(rate: ExchangeRate) {
        viewModelScope.launch {
            val favoriteRates = favoriteRatesInteractor.getFavoriteRates().firstOrNull()
            if (favoriteRates.isNullOrEmpty()) {
                favoriteRatesInteractor.addFavorite(rate)
            } else {
                if (favoriteRates.any { it.id == rate.id }) {
                    favoriteRatesInteractor.removeFavorite(rate)
                } else {
                    favoriteRatesInteractor.addFavorite(rate)
                }
            }
        }
    }

    private fun handleRetry(e: Throwable) = setState(HomeUiState.Retry(retryMsg = e.message))
    private fun handleAutoRetry(e: Throwable) = setState(HomeUiState.AutoRetry(e.message))
    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Retry -> {
                setState(HomeUiState.Loading)
                fetchRates()
            }
            is HomeUiEvent.OnFavorite -> handleFavorite(event.rate)
        }
    }
}

sealed class HomeUiState(
    val rates: List<ExchangeRate> = emptyList(),
    val favoriteRates: List<ExchangeRate> = emptyList(),
    val isLoading: Boolean = false,
    val isRetry: Boolean = false,
    val retryMsg: String? = null,
    val isAutoRetry: Boolean = false,
    val autoRetryMsg: String? = null,
    val isLoaded: Boolean = false
) : UIState {
    object Loading : HomeUiState(isLoading = true)

    class Retry(retryMsg: String? = null) : HomeUiState(isRetry = true, retryMsg = retryMsg)

    class AutoRetry(autoRetryMsg: String? = null) :
        HomeUiState(isAutoRetry = true, autoRetryMsg = autoRetryMsg)

    class Loaded(rates: List<ExchangeRate>, favoriteRates: List<ExchangeRate>) :
        HomeUiState(isLoaded = true, rates = rates, favoriteRates = favoriteRates)
}

sealed interface HomeUiEvent : UIEvent {
    object Retry : HomeUiEvent
    class OnFavorite(val rate: ExchangeRate) : HomeUiEvent
}