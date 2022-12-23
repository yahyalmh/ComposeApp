package com.example.home

import androidx.lifecycle.viewModelScope
import com.example.common.model.ExchangeRate
import com.example.core.base.BaseViewModel
import com.example.core.base.UIEvent
import com.example.core.base.UIState
import com.example.core.ext.retryWithPolicy
import com.example.favorite.FavoriteRatesInteractor
import com.example.home.util.Constant
import com.example.rate.interactor.ExchangeRateInteractor
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
        viewModelScope.launch {
            combine(
                exchangeRateInteractor.getLiveRates(Constant.liveRateFetchInterval),
                favoriteRatesInteractor.getFavoriteRates()
            ) { rates, favoriteRates ->
                    setState(HomeUiState.Loaded(rates = rates, favoriteRates = favoriteRates))
                }
                .retryWithPolicy { handleRetry() }
                .catch { e -> handleError(e) }
                .launchIn(viewModelScope)
        }
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
        val errorMessage = e.message ?: "Error while fetching the exchange rates"
        setState(HomeUiState.Retry(retryMsg = errorMessage))
    }

    private fun handleRetry() {
        val retryMsg = "Loading data is failed"
        setState(HomeUiState.AutoRetry(retryMsg))
    }

    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Retry -> {
                fetchRates()
                setState(HomeUiState.Loading)
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
) : UIState {
    object Loading : HomeUiState(isLoading = true)

    class Retry(retryMsg: String) : HomeUiState(isRetry = true, retryMsg = retryMsg)

    class AutoRetry(autoRetryMsg: String) : HomeUiState(isAutoRetry = true, retryMsg = autoRetryMsg)

    class Loaded(rates: List<ExchangeRate>, favoriteRates: List<ExchangeRate>) :
        HomeUiState(rates = rates, favoriteRates = favoriteRates)
}

sealed interface HomeUiEvent : UIEvent {
    object Retry : HomeUiEvent
    class OnFavorite(val rate: ExchangeRate) : HomeUiEvent
}