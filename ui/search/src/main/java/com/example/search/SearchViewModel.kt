package com.example.search

import androidx.lifecycle.viewModelScope
import com.example.common.BaseViewModel
import com.example.common.SharedState
import com.example.common.UIEvent
import com.example.common.UIState
import com.example.common.ext.retryWithPolicy
import com.example.common.model.ExchangeRate
import com.example.favorite.FavoriteRatesInteractor
import com.example.rate.ExchangeRateInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val exchangeRateInteractor: ExchangeRateInteractor,
    private val favoriteRatesInteractor: FavoriteRatesInteractor,
) : BaseViewModel<SearchUiState, SearchUiEvent>(SearchUiState.Start()) {
    private val searchFlow = MutableStateFlow(state.value.query)
    lateinit var searchJob: Job

    init {
        changeBottomBarVisibility(false)
        observeQueryChange()
    }

    private fun changeBottomBarVisibility(enabled: Boolean) {
        viewModelScope.launch {
            SharedState.bottomBarVisible.emit(enabled)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQueryChange() {
        searchFlow
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query ->
                if (::searchJob.isInitialized && searchJob.isActive) {
                    searchJob.cancel()
                }

                when {
                    query.isBlank() && query.isEmpty() -> setState(SearchUiState.Start())
                    query.isNotResetState() -> searchQuery(query)
                }
            }.launchIn(viewModelScope)
    }

    private fun searchQuery(query: String) {
        setState(SearchUiState.Loading(query = state.value.query))
        searchJob =
            combine(
                exchangeRateInteractor.getRates(),
                favoriteRatesInteractor.getFavoriteRates()
            ) { rates, favoriteRates ->
                val result = rates.filter {
                    it.symbol.contains(query, ignoreCase = true)
                }
                when {
                    result.isEmpty() -> setState(SearchUiState.Empty(state.value.query))
                    else -> setState(
                        SearchUiState.Loaded(
                            result = result,
                            favoriteRates = favoriteRates,
                            state.value.query
                        )
                    )
                }
            }
                .retryWithPolicy { e -> handleRetry(e) }
                .catch { e -> handleError(e) }
                .launchIn(viewModelScope)
    }

    private fun handleError(e: Throwable) {
        searchFlow.value = restString
        val errorMessage = e.message ?: "Error while fetching the exchange rate"
        setState(SearchUiState.Retry(errorMessage, query = state.value.query))
    }

    private fun handleRetry(e: Throwable) {
        val autoRetryMsg = e.message ?: "No result for \"${state.value.query}\""
        setState(SearchUiState.AutoRetry(autoRetryMsg, query = state.value.query))
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

    override fun onEvent(event: SearchUiEvent) {
        when (event) {
            SearchUiEvent.NavigationBack -> changeBottomBarVisibility(true)
            SearchUiEvent.Retry -> {
                searchFlow.value = state.value.query
                setState(SearchUiState.Search(state.value.query))
            }
            is SearchUiEvent.QueryChange -> {
                searchFlow.tryEmit(event.text)
                setState(SearchUiState.Search(event.text))
            }
            SearchUiEvent.ClearSearch -> {
                searchFlow.value = ""
                setState(SearchUiState.Empty())
            }
            is SearchUiEvent.OnFavorite -> handleFavorite(event.rate)
        }
    }
}

fun String.isNotResetState() = this != restString
const val restString = "__ResetString__"

sealed interface SearchUiEvent : UIEvent {
    object Retry : SearchUiEvent
    class QueryChange(val text: String) : SearchUiEvent
    object ClearSearch : SearchUiEvent
    object NavigationBack : SearchUiEvent
    class OnFavorite(val rate: ExchangeRate) : SearchUiEvent
}

sealed class SearchUiState(
    val result: List<ExchangeRate> = emptyList(),
    val favoriteRates: List<ExchangeRate> = emptyList(),
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val isKeyboardHidden: Boolean = false,
    val isRetry: Boolean = false,
    val isAutoRetry: Boolean = false,
    val isEmpty: Boolean = false,
    val isStart: Boolean = false,
    val retryMsg: String = "",
    val autoRetryMsg: String = "",
    var query: String
) : UIState {
    class Loading(query: String) : SearchUiState(isLoading = true, query = query)

    class Retry(retryMsg: String, query: String) : SearchUiState(
        isRetry = true,
        retryMsg = retryMsg,
        isKeyboardHidden = true,
        query = query
    )

    class AutoRetry(autoRetryMsg: String, query: String) : SearchUiState(
        isAutoRetry = true,
        isKeyboardHidden = true,
        autoRetryMsg = autoRetryMsg,
        query = query
    )

    class Search(text: String) : SearchUiState(query = text, isLoading = true)

    class Empty(query: String = "") : SearchUiState(isEmpty = true, query = query)

    class Start(query: String = "") : SearchUiState(isStart = true, query = query)

    class Loaded(
        result: List<ExchangeRate>,
        favoriteRates: List<ExchangeRate>,
        query: String
    ) :
        SearchUiState(
            isLoaded = true,
            result = result,
            favoriteRates = favoriteRates,
            query = query
        )
}