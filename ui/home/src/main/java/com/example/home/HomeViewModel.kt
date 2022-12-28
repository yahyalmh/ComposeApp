package com.example.home

import androidx.lifecycle.viewModelScope
import com.example.common.BaseViewModel
import com.example.common.UIEvent
import com.example.common.UIState
import com.example.common.ext.retryWithPolicy
import com.example.common.model.ExchangeRate
import com.example.favorite.FavoriteRatesInteractor
import com.example.home.util.Constant
import com.example.rate.ExchangeRateInteractor
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
class HomeViewModel @Inject constructor(
    private val exchangeRateInteractor: ExchangeRateInteractor,
    private val favoriteRatesInteractor: FavoriteRatesInteractor
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
            .retryWithPolicy { e -> handleRetry(e) }
            .catch { e -> handleError(e) }
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

    private fun handleError(e: Throwable) {
        println(e.printStackTrace())
        val errorMessage = e.message ?: "Error while fetching the exchange rates"
        setState(HomeUiState.Retry(retryMsg = errorMessage))
    }

    private fun handleRetry(e: Throwable) {
        val retryMsg = e.message ?: "Loading data is failed"
        setState(HomeUiState.AutoRetry(retryMsg))
    }

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
    val retryMsg: String = "",
    val isAutoRetry: Boolean = false,
    val autoRetryMsg: String = "",
    val isLoaded: Boolean = false
) : UIState {
    object Loading : HomeUiState(isLoading = true)

    class Retry(retryMsg: String) : HomeUiState(isRetry = true, retryMsg = retryMsg)

    class AutoRetry(autoRetryMsg: String) :
        HomeUiState(isAutoRetry = true, autoRetryMsg = autoRetryMsg)

    class Loaded(rates: List<ExchangeRate>, favoriteRates: List<ExchangeRate>) :
        HomeUiState(isLoaded = true, rates = rates, favoriteRates = favoriteRates)
}

sealed interface HomeUiEvent : UIEvent {
    object Retry : HomeUiEvent
    class OnFavorite(val rate: ExchangeRate) : HomeUiEvent
}