package com.example.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.data.common.model.ExchangeDetailRate
import com.example.data.common.model.toExchangeRate
import com.example.detail.nav.DetailArgs
import com.example.favorite.FavoriteRatesInteractor
import com.example.rate.ExchangeRateInteractor
import com.example.ui.common.BaseViewModel
import com.example.ui.common.SharedState
import com.example.ui.common.UIEvent
import com.example.ui.common.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author yaya (@yahyalmh)
 * @since 10th November 2022
 */

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val exchangeRateInteractor: ExchangeRateInteractor,
    private val favoriteRatesInteractor: FavoriteRatesInteractor,
) : BaseViewModel<DetailUiState, DetailUiEvent>(DetailUiState.Loading) {
    private val detailArgs: DetailArgs = DetailArgs(savedStateHandle)

    init {
        changeBottomBarVisibility(false)
        fetchRateDetail()
    }

    private fun changeBottomBarVisibility(enabled: Boolean) {
        viewModelScope.launch {
            SharedState.bottomBarVisible.emit(enabled)
        }
    }

    private fun fetchRateDetail() {
        combine(
            exchangeRateInteractor.getLiveRate(detailArgs.rateId),
            favoriteRatesInteractor.getFavoriteRates()
        ) { rate, favoritesRate ->
            setState(
                DetailUiState.Loaded(
                    rate = rate,
                    favoritesRate.any { it.id == rate.id })
            )
        }
            .catch { e -> handleError(e) }
            .launchIn(viewModelScope)
    }

    private fun handleError(e: Throwable) {
        val errorMessage = e.message ?: "Error while fetching the exchange rate"
        setState(DetailUiState.Retry(errorMessage))
    }

    private fun handleFavoriteClick(rate: ExchangeDetailRate?) {
        viewModelScope.launch {
            rate?.toExchangeRate()?.let { exchangeRate ->
                val favoriteRates = favoriteRatesInteractor.getFavoriteRates().firstOrNull()
                when {
                    favoriteRates.isNullOrEmpty() -> favoriteRatesInteractor.addFavorite(
                        exchangeRate
                    )
                    else -> {
                        if (favoriteRates.any { it.id == exchangeRate.id }) {
                            favoriteRatesInteractor.removeFavorite(exchangeRate)
                        } else {
                            favoriteRatesInteractor.addFavorite(exchangeRate)
                        }
                    }
                }
            }
        }
    }

    override fun onEvent(event: DetailUiEvent) {
        when (event) {
            DetailUiEvent.Retry -> {
                fetchRateDetail()
                setState(DetailUiState.Loading)
            }
            DetailUiEvent.NavigationBack -> changeBottomBarVisibility(true)
            is DetailUiEvent.OnFavoriteClick -> handleFavoriteClick(event.rate)

        }
    }
}

sealed interface DetailUiEvent : UIEvent {
    object Retry : DetailUiEvent
    object NavigationBack : DetailUiEvent
    class OnFavoriteClick(val rate: ExchangeDetailRate?) : DetailUiEvent

}

sealed class DetailUiState(
    val rateDetail: ExchangeDetailRate? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val isError: Boolean = false,
    val errorMsg: String = "",
) : UIState {
    object Loading : DetailUiState(isLoading = true)

    class Retry(errorMsg: String) : DetailUiState(
        isError = true,
        errorMsg = errorMsg
    )

    class Loaded(rate: ExchangeDetailRate, isFavorite: Boolean) : DetailUiState(
        isLoaded = true,
        rateDetail = rate,
        isFavorite = isFavorite,
    )
}