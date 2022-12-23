package com.example.search

import androidx.lifecycle.viewModelScope
import com.example.common.SharedState
import com.example.common.model.ExchangeRate
import com.example.core.base.BaseViewModel
import com.example.core.base.UIEvent
import com.example.core.base.UIState
import com.example.core.ext.retryWithPolicy
import com.example.favorite.FavoriteRatesInteractor
import com.example.rate.interactor.ExchangeRateInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val exchangeRateInteractor: ExchangeRateInteractor,
    private val favoriteRatesInteractor: FavoriteRatesInteractor,
) : BaseViewModel<SearchUiState, SearchUiEvent>(SearchUiState.Empty()) {
    private val searchFlow = MutableStateFlow(state.value.query)
    lateinit var job: Job

    init {
        changeBottomBarVisibility(false)
        observeQueryChange()
    }

    private fun changeBottomBarVisibility(enabled: Boolean) {
        viewModelScope.launch {
            SharedState.bottomBarVisible.emit(enabled)
        }
    }

    private fun observeQueryChange() {
        searchFlow
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query ->
                if (::job.isInitialized && job.isActive) {
                    job.cancel()
                }

                when {
                    query.isBlank() && query.isEmpty() -> setState(SearchUiState.Empty())
                    query.isNotResetState() -> searchQuery(query)
                }
            }.launchIn(viewModelScope)
    }

    private fun searchQuery(query: String) {
        job =
            viewModelScope.launch {
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
                    .retryWithPolicy { handleRetry() }
                    .catch { e -> handleError(e) }
                    .launchIn(viewModelScope)
            }
    }

    private fun handleError(e: Throwable) {
        searchFlow.value = restString
        val errorMessage = e.message ?: "Error while fetching the exchange rate"
        setState(SearchUiState.Error(errorMessage, query = state.value.query))
    }

    private fun handleRetry() {
        val retryMsg = "No result for \"${state.value.query}\""
        setState(SearchUiState.Retry(retryMsg, query = state.value.query))
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
    val isRetry: Boolean = false,
    val isAutoRetry: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMsg: String = "",
    val retryMsg: String = "",
    var query: String
) : UIState {
    class Loading(query: String) : SearchUiState(isLoading = true, query = query)

    class Error(errorMsg: String, query: String) : SearchUiState(
        isRetry = true,
        errorMsg = errorMsg,
        query = query
    )

    class Retry(retryMsg: String, query: String) : SearchUiState(
        isAutoRetry = true,
        retryMsg = retryMsg,
        query = query
    )

    class Search(text: String) : SearchUiState(query = text, isLoading = true)

    class Empty(query: String = "") : SearchUiState(isEmpty = true, query = query)

    class Loaded(
        result: List<ExchangeRate>,
        favoriteRates: List<ExchangeRate>,
        query: String
    ) :
        SearchUiState(result = result, favoriteRates = favoriteRates, query = query)
}