package com.example.favorite

import androidx.lifecycle.viewModelScope
import com.example.common.model.ExchangeRate
import com.example.common.BaseViewModel
import com.example.common.UIEvent
import com.example.common.UIState
import com.example.common.ext.retryWithPolicy
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
        fetchFavorite()
    }

    private fun fetchFavorite() {
        viewModelScope.launch {
            favoriteRatesInteractor.getLiveFavoriteRates()
                .retryWithPolicy { handleRetry() }
                .catch { e -> handleError(e) }
                .onEach {result ->
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
            if (favoriteRates.isNullOrEmpty()){
                favoriteRatesInteractor.addFavorite(rate)
            }else{
                if (favoriteRates.any { it.id == rate.id }){
                    favoriteRatesInteractor.removeFavorite(rate)
                }else{
                    favoriteRatesInteractor.addFavorite(rate)
                }
            }
        }
    }


    private fun handleError(e: Throwable) {
        val errorMessage = e.message ?: "Error while fetching the exchange rates"
        setState(FavoriteUiState.Retry(retryMsg = errorMessage))
    }

    private fun handleRetry() {
        val retryMsg = "Loading data is failed"
        setState(FavoriteUiState.AutoRetry(retryMsg))
    }

    override fun onEvent(event: FavoriteUiEvent) {
        when (event) {
            FavoriteUiEvent.Retry -> {
                fetchFavorite()
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
    val isEmpty: Boolean = false
) : UIState {

    object Loading : FavoriteUiState(isLoading = true)

    class Retry(retryMsg: String) : FavoriteUiState(isRetry = true, retryMsg = retryMsg)

    class AutoRetry(autoRetryMsg: String) : FavoriteUiState(isAutoRetry = true, retryMsg = autoRetryMsg)

    class Loaded(rates: List<ExchangeRate>) : FavoriteUiState(rates = rates)

    class Empty(query: String = "") : FavoriteUiState(isEmpty = true)

}
