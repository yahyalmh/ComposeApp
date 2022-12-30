package com.example.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.ui.common.ReferenceDevices
import com.example.ui.common.component.BaseLazyColumn
import com.example.ui.common.component.cell.toCell
import com.example.ui.common.component.icon.AppIcons
import com.example.ui.common.component.screen.TopBarScaffold
import com.example.ui.common.component.view.AutoRetryView
import com.example.ui.common.component.view.LoadingView
import com.example.ui.common.component.view.RetryView
import com.example.data.common.model.ExchangeRate
import com.example.detail.nav.navigateToDetail
import com.example.search.nav.navigateToSearch
import com.example.ui.home.R

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    HomeScreenContent(
        modifier = modifier,
        navController = navController,
        uiState = viewModel.state.value,
        onRetry = { viewModel.onEvent(HomeUiEvent.Retry) },
        onFavoriteClick = { rate -> viewModel.onEvent(HomeUiEvent.OnFavorite(rate)) }
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    uiState: HomeUiState,
    onRetry: () -> Unit = {},
    onFavoriteClick: (rate: ExchangeRate) -> Unit = {},
) {
    TopBarScaffold(
        title = stringResource(id = R.string.home),
        navigationIcon = AppIcons.Menu,
        navigationIconContentDescription = stringResource(id = R.string.menu),
        actionIcon = AppIcons.Search,
        actionIconContentDescription = stringResource(id = R.string.searchIconContentDescription),
        onActionClick = { navController.navigateToSearch() }
    ) { padding ->

        LoadingView(
            modifier = Modifier.padding(padding),
            isVisible = uiState.isLoading
        )

        RetryView(
            isVisible = uiState.isRetry,
            retryMessage = uiState.retryMsg,
            icon = AppIcons.Warning,
            onRetry = onRetry
        )

        AutoRetryView(
            isVisible = uiState.isAutoRetry,
            errorMessage = uiState.autoRetryMsg,
            icon = AppIcons.Warning,
        )

        DataView(
            isVisible = uiState.isLoaded,
            modifier = modifier.padding(padding),
            rates = uiState.rates,
            favoritesRates = uiState.favoriteRates,
            navigateToDetail = { rateId -> navController.navigateToDetail(rateId) },
            onFavoriteClick = onFavoriteClick
        )
    }
}

@Composable
private fun DataView(
    modifier: Modifier,
    isVisible: Boolean,
    rates: List<ExchangeRate>,
    favoritesRates: List<ExchangeRate>,
    navigateToDetail: (id: String) -> Unit,
    onFavoriteClick: (rate: ExchangeRate) -> Unit
) {
    val models = rates.map {
        it.toCell(
            favoritesRates = favoritesRates,
            navigateToDetail = navigateToDetail,
            onFavoriteClick = onFavoriteClick
        )
    }
    BaseLazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        isVisible = isVisible,
        models = models
    )
}

@Composable
@ReferenceDevices
fun DataPreview() = DataView(
    modifier = Modifier,
    rates = ratesStub(),
    favoritesRates = ratesStub(10),
    isVisible = true,
    navigateToDetail = {}
) {}

@Composable
private fun ratesStub(count: Int = 20): MutableList<ExchangeRate> {
    val rates = mutableListOf<ExchangeRate>()
    repeat(count) { rates.add(rateStub()) }
    return rates
}

@Composable
internal fun rateStub(): ExchangeRate = ExchangeRate(
    id = "1",
    symbol = "$",
    currencySymbol = "USD",
    type = "fiat",
    rateUsd = 0.165451654889.toBigDecimal()
)