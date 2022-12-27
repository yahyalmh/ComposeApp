package com.example.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.common.component.cell.RateCell
import com.example.common.component.*
import com.example.common.component.bar.TopAppBar
import com.example.common.component.icon.AppIcons
import com.example.common.model.ExchangeRate
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
            TopAppBar(
                title = stringResource(id = R.string.favorite),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
            )
        }
    ) { padding ->

        LoadingView(isVisible = uiState.isLoading)

        EmptyView(
            modifier = Modifier, isVisible = uiState.isEmpty,
            icon = AppIcons.FavoriteBorder,
            message = stringResource(id = R.string.noFavoriteItemFound)
        )

        RetryView(
            isVisible = uiState.isRetry,
            errorMessage = uiState.retryMsg,
            icon = AppIcons.Warning
        ) {
            viewModel.onEvent(FavoriteUiEvent.Retry)
        }

        AutoRetryView(
            isVisible = uiState.isAutoRetry,
            errorMessage = uiState.autoRetryMsg,
            icon = AppIcons.Warning,
            hint = stringResource(id = R.string.autoRetryHint)
        )

        ContentView(
            isVisible = uiState.isLoaded,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            rates = uiState.rates,
            navigateToDetail = { rateId -> navController.navigateToDetail(rateId) },
            onFavoriteClick = { rate -> viewModel.onEvent(FavoriteUiEvent.OnFavorite(rate)) }
        )
    }
}

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
            rate = rate,
            onClick = { navigateToDetail(rate.id) },
            leadingIcon = AppIcons.Favorite,
            onLeadingIconClick = onFavoriteClick
        )
    }
}