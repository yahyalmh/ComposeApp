package com.example.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.common.database.ExchangeRateEntity
import com.example.common.model.ExchangeRate
import com.example.core.base.ReferenceDevices
import com.example.core.component.AutoRetryView
import com.example.core.component.BaseLazyColumn
import com.example.core.component.LoadingView
import com.example.core.component.RetryView
import com.example.core.component.bar.AppBar
import com.example.core.component.icon.AppIcons
import com.example.detail.nav.navigateToDetail
import com.example.search.nav.navigateToSearch
import com.example.ui.home.R
import com.example.core.R.string as coreR


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
                navigationIconContentDescription = stringResource(id = coreR.menu),
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
            hint = stringResource(id = coreR.autoRetryHint)
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

@OptIn(ExperimentalMaterial3Api::class)
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
            rate = rate.rateUsd.toString(),
            symbol = rate.symbol,
            currencySymbol = rate.currencySymbol ?: rate.symbol,
            type = rate.type,
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .size(AssistChipDefaults.IconSize)
                        .clickable { onFavoriteClick(rate) },
                    imageVector = leadingIcon,
                    contentDescription = "Like Icon",
                )
            },
            onClick = { navigateToDetail(rate.id) }
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
private fun favoriteRatesStub(count: Int = 10): MutableList<ExchangeRateEntity> {
    val rates = mutableListOf<ExchangeRateEntity>()
    repeat(count) { rates.add(favoriteRateStub()) }
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

@Composable
internal fun favoriteRateStub() = ExchangeRateEntity(
    id = "1",
    symbol = "$",
)