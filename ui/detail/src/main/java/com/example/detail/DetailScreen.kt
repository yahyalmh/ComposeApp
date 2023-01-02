package com.example.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.data.common.model.ExchangeDetailRate
import com.example.ui.common.component.icon.AppIcons
import com.example.ui.common.component.screen.TopBarScaffold
import com.example.ui.common.component.view.RetryView
import com.example.ui.common.component.view.ShimmerGradient
import com.example.ui.common.component.view.ShimmerView
import com.example.ui.common.component.view.shimmerBackground
import com.example.ui.detail.R

/**
 * @author yaya (@yahyalmh)
 * @since 10th November 2022
 */

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    DetailScreenContent(
        modifier = modifier,
        uiState = viewModel.state.value,
        onFavoriteClick = { rateDetail -> viewModel.onEvent(DetailUiEvent.OnFavoriteClick(rateDetail)) },
        onBackClick = {
            viewModel.onEvent(DetailUiEvent.NavigationBack)
            navController.popBackStack()
        },
        onRetry = { viewModel.onEvent(DetailUiEvent.Retry) }
    )
}

@Composable
private fun DetailScreenContent(
    modifier: Modifier,
    uiState: DetailUiState,
    onFavoriteClick: (rate: ExchangeDetailRate?) -> Unit,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
) {
    TopBarScaffold(
        title = stringResource(id = R.string.detail),
        actionIcon = if (uiState.isFavorite) {
            AppIcons.Favorite
        } else {
            AppIcons.FavoriteBorder
        },
        actionIconContentDescription = stringResource(id = R.string.favoriteIcon),
        onActionClick = { onFavoriteClick(uiState.rateDetail) },
        actionIconColor = Color.Red,
        onNavigationClick = { onBackClick() }
    ) { padding ->

        DetailShimmerView(modifier = modifier.padding(padding), isVisible = uiState.isLoading)

        RetryView(
            modifier = modifier.padding(padding),
            isVisible = uiState.isError,
            retryMessage = uiState.errorMsg,
            icon = AppIcons.Warning,
            onRetry = onRetry
        )

        DataView(
            modifier = modifier.padding(padding),
            isVisible = uiState.isLoaded,
            rateDetail = uiState.rateDetail
        )
    }
}

@Composable
private fun DataView(
    modifier: Modifier,
    isVisible: Boolean,
    rateDetail: ExchangeDetailRate?,
) {
    if (isVisible) {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(150.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = rateDetail?.let { it.currencySymbol ?: it.symbol } ?: "",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            rateDetail?.run {
                Text(
                    modifier = modifier.padding(top = 10.dp),
                    textAlign = TextAlign.Center,
                    text = "Rate Usd: ${rateUsd.toPlainString()}",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    modifier = Modifier.padding(vertical = 20.dp),
                    text = "Symbol: $symbol",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    modifier = Modifier.padding(bottom = 10.dp),
                    text = "Type: $type",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "TimeStamp: $timestamp",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

@Composable
fun DetailShimmerView(
    modifier: Modifier = Modifier,
    isVisible: Boolean
) {
    ShimmerView(
        modifier = modifier,
        isVisible = isVisible,
    ) { shimmerAxis ->
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                space = 30.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(150.dp)
                    .shimmerBackground(
                        gradient = ShimmerGradient.Linear(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shimmerAxis = shimmerAxis
                        )
                    )
                    .padding(vertical = 15.dp),
            )
            Box(
                modifier = Modifier
                    .size(270.dp, 22.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .shimmerBackground(
                        gradient = ShimmerGradient.Linear(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shimmerAxis = shimmerAxis
                        )
                    ),
            )
            Box(
                modifier = Modifier
                    .size(150.dp, 18.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .shimmerBackground(
                        gradient = ShimmerGradient.Linear(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shimmerAxis = shimmerAxis
                        )
                    ),
            )
            Box(
                modifier = Modifier
                    .size(120.dp, 18.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .shimmerBackground(
                        gradient = ShimmerGradient.Linear(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shimmerAxis = shimmerAxis
                        )
                    ),
            )
            Box(
                modifier = Modifier
                    .size(240.dp, 18.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .shimmerBackground(
                        gradient = ShimmerGradient.Linear(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shimmerAxis = shimmerAxis
                        )
                    ),
            )
        }
    }
}

@Preview
@Composable
fun DetailShimmerPreview() {
    DetailShimmerView(isVisible = true)
}

@Composable
@Preview
fun ContentPreview() {
    val rateDetail = ExchangeDetailRate(
        "Id",
        symbol = "$",
        currencySymbol = "##",
        type = "Fiat",
        rateUsd = 0.16544654.toBigDecimal(),
        timestamp = 1324654312
    )
    DataView(modifier = Modifier, isVisible = true, rateDetail = rateDetail)
}