package com.example.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.common.RateCell
import com.example.common.ReferenceDevices
import com.example.common.component.AutoRetryView
import com.example.common.component.BaseLazyColumn
import com.example.common.component.LoadingView
import com.example.common.component.RetryView
import com.example.common.component.bar.AppBar
import com.example.common.component.icon.AppIcons
import com.example.common.model.ExchangeRate
import com.example.detail.nav.navigateToDetail
import com.example.search.nav.navigateToSearch
import com.example.ui.home.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            AppBar(
                titleRes = R.string.home,
                modifier = Modifier
                    .zIndex(1F)
                    .shadow(
                        elevation = 5.dp,
                        spotColor = MaterialTheme.colorScheme.onBackground
                    ),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = AppIcons.Menu,
                navigationIconContentDescription = stringResource(id = R.string.menu),
                actionIcon = AppIcons.Search,
                actionIconContentDescription = stringResource(id = R.string.searchIcon),
                onActionClick = { navController.navigateToSearch() }
            )
        }
    ) { padding ->
        val uiState = viewModel.state.value

        LoadingView(isLoading = uiState.isLoading)

        RetryView(
            isVisible = uiState.isRetry,
            errorMessage = uiState.retryMsg,
            icon = AppIcons.Warning
        ) {
            viewModel.onEvent(HomeUiEvent.Retry)
        }

        AutoRetryView(
            isVisible = uiState.isAutoRetry,
            errorMessage = uiState.retryMsg,
            icon = AppIcons.Warning,
        )

        ContentView(
            isVisible = uiState is HomeUiState.Loaded,
            modifier = modifier
                .padding(padding),
            rates = uiState.rates,
            favoritesRates = uiState.favoriteRates,
            navigateToDetail = { rateId -> navController.navigateToDetail(rateId) }
        ) { rate -> viewModel.onEvent(HomeUiEvent.OnFavorite(rate)) }
    }
}

@Composable
private fun ContentView(
    modifier: Modifier,
    isVisible: Boolean,
    rates: List<ExchangeRate>,
    favoritesRates: List<ExchangeRate>,
    navigateToDetail: (id: String) -> Unit,
    onFavoriteClick: (rate: ExchangeRate) -> Unit
) {
    BaseLazyColumn(
        isVisible = isVisible, items = rates,
        modifier = modifier.background(MaterialTheme.colorScheme.surface)
    ) { rate ->
        val leadingIcon = if (favoritesRates.any { it.id == rate.id && it.symbol == rate.symbol }) {
            AppIcons.Favorite
        } else {
            AppIcons.FavoriteBorder
        }
        RateCell(
            rate = rate,
            leadingIcon = leadingIcon,
            onClick = { navigateToDetail(rate.id) },
            onLeadingIconClick = onFavoriteClick
        )
    }
}

@Composable
@ReferenceDevices
fun ContentPreview() = ContentView(
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