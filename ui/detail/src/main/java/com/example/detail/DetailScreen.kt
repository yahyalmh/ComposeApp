package com.example.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.common.model.ExchangeDetailRate
import com.example.core.base.ReferenceDevices
import com.example.core.component.LoadingView
import com.example.core.component.RetryView
import com.example.core.component.bar.AppBar
import com.example.core.component.icon.Icons
import com.example.ui.detail.R

/**
 * @author yaya (@yahyalmh)
 * @since 10th November 2022
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    Scaffold(
        contentColor = MaterialTheme.colorScheme.surface,
        topBar = {
            AppBar(
                titleRes = R.string.detail,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actionIcon = if (state.isFavorite) {
                    Icons.Favorite
                } else {
                    Icons.FavoriteBorder
                },
                onActionClick = {viewModel.onEvent(DetailUiEvent.OnFavorite(state.rate)) },
                actionIconContentDescription = stringResource(id = R.string.favoriteIcon),
                navigationIcon = Icons.ArrowBack,
                navigationIconContentDescription = "Back",
                onNavigationClick = {
                    viewModel.onEvent(DetailUiEvent.NavigationBack)
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->

        LoadingView(isLoading = state.isLoading)

        RetryView(
            isVisible = state.isError,
            errorMessage = state.errorMsg,
            icon = Icons.Warning
        ) { viewModel.onEvent(DetailUiEvent.Retry) }

        ContentView(modifier.padding(padding), state)
    }
}

@Composable
private fun ContentView(
    modifier: Modifier,
    state: DetailUiState
) {
    if (state is DetailUiState.Loaded) {
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
                Text(text = state.rate?.let { it.currencySymbol ?: it.symbol } ?: "",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            state.rate?.run {
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
@ReferenceDevices
fun ContentPreview() {
    val rateDetail = ExchangeDetailRate(
        "Id",
        symbol = "$",
        currencySymbol = "##",
        type = "Fiat",
        rateUsd = 0.16544654.toBigDecimal(),
        timestamp = 1324654312
    )
    val state = DetailUiState.Loaded(rateDetail, true)
    ContentView(modifier = Modifier, state = state)
}