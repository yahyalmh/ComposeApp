package com.example.search

import androidx.lifecycle.viewModelScope
import com.example.data.common.model.ExchangeRate
import com.example.favorite.FavoriteRatesInteractor
import com.example.rate.ExchangeRateInteractor
import com.example.ui.common.BaseViewModel
import com.example.ui.common.SharedState
import com.example.ui.common.UIEvent
import com.example.ui.common.UIState
import com.example.ui.common.connectivity.ConnectivityMonitor
import com.example.ui.common.ext.retryOnNetworkConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val exchangeRateInteractor: ExchangeRateInteractor,
    private val favoriteRatesInteractor: FavoriteRatesInteractor,
    private val connectivityMonitor: ConnectivityMonitor,
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

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeQueryChange() {
        searchFlow
            .debounce(300)
            .distinctUntilChanged()
            .mapLatest { query ->
                when {
                    query.isBlank() && query.isEmpty() -> setState(SearchUiState.Start())
                    query.isNotResetState() -> {
                        if (::searchJob.isInitialized && searchJob.isActive) {
                            searchJob.cancel()
                        }
                        searchQuery(query)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun searchQuery(query: String) {
        setState(SearchUiState.Loading(query = query))
        searchJob = combine(
            exchangeRateInteractor.getRates().map { rates ->
                rates.filter {
                    it.symbol.contains(query, ignoreCase = true)
                }
            },
            favoriteRatesInteractor.getFavoriteRates()
        ) { result, favoriteRates ->
            when {
                result.isEmpty() -> setState(SearchUiState.Empty(state.value.query))
                else -> setState(
                    SearchUiState.Loaded(
                        result = result,
                        favoriteRates = favoriteRates,
                        query = state.value.query
                    )
                )
            }
        }
            .retryOnNetworkConnection(connectivityMonitor) { e -> handleAutoRetry(e) }
            .catch { e -> handleError(e) }
            .launchIn(viewModelScope)
    }

    private fun handleError(e: Throwable) {
        searchFlow.value = restString
        setState(SearchUiState.Retry(e.message, query = state.value.query))
    }

    private fun handleAutoRetry(e: Throwable) {
        searchFlow.value = restString
        setState(SearchUiState.AutoRetry(e.message, query = state.value.query))
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
            }
            is SearchUiEvent.QueryChange -> {
                searchFlow.tryEmit(event.text)
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
    val retryMsg: String? = null,
    val autoRetryMsg: String? = null,
    var query: String
) : UIState {
    class Loading(query: String) : SearchUiState(isLoading = true, query = query)

    class Retry(retryMsg: String? = null, query: String) : SearchUiState(
        isRetry = true,
        retryMsg = retryMsg,
        isKeyboardHidden = true,
        query = query
    )

    class AutoRetry(autoRetryMsg: String? = null, query: String) : SearchUiState(
        isAutoRetry = true,
        isKeyboardHidden = true,
        autoRetryMsg = autoRetryMsg,
        query = query
    )

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