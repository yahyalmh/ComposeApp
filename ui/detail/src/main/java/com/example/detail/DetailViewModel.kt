package com.example.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.common.SharedState
import com.example.common.model.ExchangeDetailRate
import com.example.common.model.toExchangeRate
import com.example.common.BaseViewModel
import com.example.common.UIEvent
import com.example.common.UIState
import com.example.detail.nav.DetailArgs
import com.example.favorite.FavoriteRatesInteractor
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
        fetchRate()
    }

    private fun changeBottomBarVisibility(enabled: Boolean) {
        viewModelScope.launch {
            SharedState.bottomBarVisible.emit(enabled)
        }
    }

    private fun fetchRate() {
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
            .catch { e ->
                handleError(e)
            }.launchIn(viewModelScope)
    }

    private fun handleError(e: Throwable) {
        val errorMessage = e.message ?: "Error while fetching the exchange rate"
        setState(DetailUiState.Error(errorMessage))
    }

    private fun handleFavorite(rate: ExchangeDetailRate?) {
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
                fetchRate()
                setState(DetailUiState.Loading)
            }
            DetailUiEvent.NavigationBack -> changeBottomBarVisibility(true)
            is DetailUiEvent.OnFavorite -> handleFavorite(event.rate)

        }
    }
}

sealed interface DetailUiEvent : UIEvent {
    object Retry : DetailUiEvent
    object NavigationBack : DetailUiEvent
    class OnFavorite(val rate: ExchangeDetailRate?) : DetailUiEvent

}

sealed class DetailUiState(
    val rate: ExchangeDetailRate? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMsg: String = "",
) : UIState {
    object Loading : DetailUiState(isLoading = true)

    class Error(errorMsg: String) : DetailUiState(
        isError = true,
        errorMsg = errorMsg
    )

    class Loaded(rate: ExchangeDetailRate, isFavorite: Boolean) : DetailUiState(
        rate = rate,
        isFavorite = isFavorite,
    )
}