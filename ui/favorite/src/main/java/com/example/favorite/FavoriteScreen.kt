package com.example.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.data.common.model.ExchangeRate
import com.example.detail.nav.navigateToDetail
import com.example.ui.common.component.BaseLazyColumn
import com.example.ui.common.component.cell.RateShimmerCell
import com.example.ui.common.component.cell.toCell
import com.example.ui.common.component.icon.AppIcons
import com.example.ui.common.component.screen.TopBarScaffold
import com.example.ui.common.component.view.AutoRetryView
import com.example.ui.common.component.view.EmptyView
import com.example.ui.common.component.view.RetryView
import com.example.ui.common.component.view.ShimmerView
import com.example.ui.common.ext.create
import com.example.ui.favorite.R

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    FavoriteScreenContent(
        modifier = modifier,
        uiState = viewModel.state.value,
        navController = navController,
        onRetry = { viewModel.onEvent(FavoriteUiEvent.Retry) },
        onFavoriteClick = { rate -> viewModel.onEvent(FavoriteUiEvent.OnFavorite(rate)) }
    )
}

@Composable
private fun FavoriteScreenContent(
    modifier: Modifier = Modifier,
    uiState: FavoriteUiState,
    navController: NavController,
    onRetry: () -> Unit,
    onFavoriteClick: (rate: ExchangeRate) -> Unit
) {
    TopBarScaffold(
        title = stringResource(id = R.string.favorite),
    ) { padding ->

        FavoriteShimmerView(modifier = modifier.padding(padding), isVisible = uiState.isLoading)

        EmptyView(
            modifier = Modifier, isVisible = uiState.isEmpty,
            icon = AppIcons.FavoriteBorder,
            message = stringResource(id = R.string.noFavoriteItemFound)
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
            hint = stringResource(id = R.string.autoRetryHint)
        )


        DataView(
            isVisible = uiState.isLoaded,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            rates = uiState.rates,
            navigateToDetail = { rateId -> navController.navigateToDetail(rateId) },
            onFavoriteClick = onFavoriteClick
        )
    }
}

@Composable
fun DataView(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    rates: List<ExchangeRate>,
    navigateToDetail: (id: String) -> Unit,
    onFavoriteClick: (rate: ExchangeRate) -> Unit
) {
    val models = rates.map {
        it.toCell(
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
fun FavoriteShimmerView(
    modifier: Modifier = Modifier,
    isVisible: Boolean
) {
    ShimmerView(
        modifier = modifier,
        isVisible = isVisible,
    ) { shimmerAxis ->
        BaseLazyColumn(
            isVisible = true,
            models = create(count = 5) { { RateShimmerCell(shimmerAxis = shimmerAxis) } }
        )
    }
}

@Preview
@Composable
fun FavoriteScreenPreview() {
    FavoriteScreenContent(
        uiState = FavoriteUiState.Loading,
        navController = rememberNavController(),
        onRetry = {},
        onFavoriteClick = {}
    )
}