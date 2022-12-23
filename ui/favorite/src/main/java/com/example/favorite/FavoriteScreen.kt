package com.example.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.common.RateCell
import com.example.common.model.ExchangeRate
import com.example.core.component.*
import com.example.core.component.bar.AppBar
import com.example.core.component.icon.Icons
import com.example.detail.nav.navigateToDetail
import com.example.ui.favorite.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavController,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.value
    Scaffold(
        contentColor = MaterialTheme.colorScheme.surface,
        topBar = {
            AppBar(
                titleRes = R.string.favorite,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
            )
        }
    ) { padding ->

        LoadingView(isLoading = uiState.isLoading)

        EmptyView(
            modifier = Modifier, isVisible = uiState.isEmpty,
            icon = Icons.Face,
            message = stringResource(id = R.string.noFavoriteItemFound)
        )

        RetryView(
            isVisible = uiState.isRetry,
            errorMessage = uiState.retryMsg,
            icon = Icons.Warning
        ) {
            viewModel.onEvent(FavoriteUiEvent.Retry)
        }

        AutoRetryView(
            isVisible = uiState.isAutoRetry,
            errorMessage = uiState.retryMsg,
            icon = Icons.Warning,
            hint = stringResource(id = R.string.autoRetryHint)
        )

        ContentView(
            isVisible = uiState is FavoriteUiState.Loaded,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            rates = uiState.rates,
            navigateToDetail = { rateId -> navController.navigateToDetail(rateId) },
            onFavoriteClick = { rate -> viewModel.onEvent(FavoriteUiEvent.OnFavorite(rate)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    rates: List<ExchangeRate>,
    navigateToDetail: (id: String) -> Unit,
    onFavoriteClick: (rate: ExchangeRate) -> Unit
) {
    BaseLazyColumn(
        isVisible = isVisible,
        items = rates,
        modifier = modifier.background(MaterialTheme.colorScheme.surface)
    ) { rate ->
        RateCell(
            rate = rate.rateUsd.toString(),
            symbol = rate.symbol,
            currencySymbol = rate.currencySymbol ?: rate.symbol,
            type = rate.type,
            onClick = { navigateToDetail(rate.id) },
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .size(AssistChipDefaults.IconSize)
                        .clickable { onFavoriteClick(rate) },
                    imageVector = Icons.Favorite,
                    contentDescription = "Like Icon",
                )
            }
        )
    }
}