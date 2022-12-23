package com.example.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.common.RateCell
import com.example.common.model.ExchangeRate
import com.example.core.component.*
import com.example.core.component.bar.SearchBar
import com.example.core.component.icon.AppIcons
import com.example.detail.nav.navigateToDetail
import com.example.ui.search.R
import com.example.core.R.string as coreR

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    LoadingView(isLoading = state.isLoading)

    AutoRetryView(
        isVisible = state.isAutoRetry,
        errorMessage = state.retryMsg,
        icon = AppIcons.Warning,
        hint = stringResource(id = coreR.autoRetryHint)
    )

    RetryView(
        isVisible = state.isRetry,
        errorMessage = state.errorMsg,
        icon = AppIcons.Warning
    ) { viewModel.onEvent(SearchUiEvent.Retry) }

    EmptyView(
        modifier = modifier,
        isVisible = state.isEmpty,
        icon = AppIcons.Search,
        message = stringResource(id = R.string.noItemFound)
    )

    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        SearchBar(
            modifier = modifier.background(MaterialTheme.colorScheme.surface),
            onQueryChange = { query -> viewModel.onEvent(SearchUiEvent.QueryChange(query)) },
            onCancelClick = {
                viewModel.onEvent(SearchUiEvent.NavigationBack)
                navController.popBackStack()
            })

        ContentView(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize(),
            state = state,
            navigateToDetail = { id -> navController.navigateToDetail(id) }
        ) { rate -> viewModel.onEvent(SearchUiEvent.OnFavorite(rate)) }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun ContentView(
    modifier: Modifier = Modifier,
    state: SearchUiState,
    navigateToDetail: (id: String) -> Unit,
    onFavoriteClick: (rate: ExchangeRate) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()
    if (lazyListState.isScrollInProgress) {
        keyboardController?.hide()
    }

    BaseLazyColumn(
        modifier = modifier,
        lazyListState = lazyListState,
        isVisible = state is SearchUiState.Loaded,
        items = state.result,
    ) { rate ->
        val leadingIcon =
            if (state.favoriteRates.any { it.id == rate.id && it.symbol == rate.symbol }) {
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
