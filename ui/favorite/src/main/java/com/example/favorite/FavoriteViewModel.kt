package com.example.favorite

import androidx.lifecycle.viewModelScope
import com.example.ui.common.BaseViewModel
import com.example.ui.common.UIEvent
import com.example.ui.common.UIState
import com.example.ui.common.ext.retryWithPolicy
import com.example.data.common.model.ExchangeRate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRatesInteractor: FavoriteRatesInteractor
) : BaseViewModel<FavoriteUiState, FavoriteUiEvent>(FavoriteUiState.Loading) {
    init {
        fetchFavoriteRates()
    }

    private fun fetchFavoriteRates() {
        viewModelScope.launch {
            favoriteRatesInteractor.getLiveFavoriteRates()
                .retryWithPolicy { e -> handleRetry(e) }
                .catch { e -> handleError(e) }
                .onEach { result ->
                    when {
                        result.isEmpty() -> setState(FavoriteUiState.Empty())
                        else -> setState(FavoriteUiState.Loaded(rates = result))
                    }
                }.launchIn(viewModelScope)
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
        setState(FavoriteUiState.Retry(retryMsg = errorMessage))
    }

    private fun handleRetry(e: Throwable) {
        val retryMsg = e.message ?: "Loading data is failed"
        setState(FavoriteUiState.AutoRetry(retryMsg))
    }

    override fun onEvent(event: FavoriteUiEvent) {
        when (event) {
            FavoriteUiEvent.Retry -> {
                fetchFavoriteRates()
                setState(FavoriteUiState.Loading)
            }
            is FavoriteUiEvent.OnFavorite -> handleFavorite(event.rate)
        }
    }
}

sealed interface FavoriteUiEvent : UIEvent {
    object Retry : FavoriteUiEvent
    class OnFavorite(val rate: ExchangeRate) : FavoriteUiEvent
}

sealed class FavoriteUiState(
    val isLoading: Boolean = false,
    val rates: List<ExchangeRate> = emptyList(),
    val isRetry: Boolean = false,
    val retryMsg: String = "",
    val isAutoRetry: Boolean = false,
    val autoRetryMsg: String = "",
    val isEmpty: Boolean = false,
    val isLoaded: Boolean = false,
) : UIState {

    object Loading : FavoriteUiState(isLoading = true)

    class Retry(retryMsg: String) : FavoriteUiState(isRetry = true, retryMsg = retryMsg)

    class AutoRetry(autoRetryMsg: String) :
        FavoriteUiState(isAutoRetry = true, autoRetryMsg = autoRetryMsg)

    class Loaded(rates: List<ExchangeRate>) : FavoriteUiState(isLoaded = true, rates = rates)

    class Empty() : FavoriteUiState(isEmpty = true)

}
