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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.common.ReferenceDevices
import com.example.common.component.LoadingView
import com.example.common.component.RetryView
import com.example.common.component.bar.TopAppBar
import com.example.common.component.icon.AppIcons
import com.example.common.model.ExchangeDetailRate
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
            TopAppBar(
                title = stringResource(id = R.string.detail),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actionIcon = if (state.isFavorite) {
                    AppIcons.Favorite
                } else {
                    AppIcons.FavoriteBorder
                },
                actionIconColor = Color.Red,
                onActionClick = { viewModel.onEvent(DetailUiEvent.OnFavoriteClick(state.rateDetail)) },
                actionIconContentDescription = stringResource(id = R.string.favoriteIcon),
                navigationIcon = AppIcons.ArrowBack,
                navigationIconContentDescription = "Back",
                onNavigationClick = {
                    viewModel.onEvent(DetailUiEvent.NavigationBack)
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->

        LoadingView(isVisible = state.isLoading)

        RetryView(
            isVisible = state.isError,
            errorMessage = state.errorMsg,
            icon = AppIcons.Warning
        ) { viewModel.onEvent(DetailUiEvent.Retry) }

        ContentView(
            modifier = modifier.padding(padding),
            isVisible = state.isLoaded,
            rateDetail = state.rateDetail
        )
    }
}

@Composable
private fun ContentView(
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
    ContentView(modifier = Modifier, isVisible = true, rateDetail = rateDetail)
}