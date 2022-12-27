package com.example.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.common.component.*
import com.example.common.component.bar.SearchBar
import com.example.common.component.cell.RateCell
import com.example.common.component.icon.AppIcons
import com.example.common.model.ExchangeRate
import com.example.detail.nav.navigateToDetail
import com.example.ui.search.R

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.value
    HandleKeyboard(uiState.isKeyboardHidden)

    LoadingView(isVisible = uiState.isLoading)

    AutoRetryView(
        isVisible = uiState.isAutoRetry,
        errorMessage = uiState.autoRetryMsg,
        icon = AppIcons.Warning,
        hint = stringResource(id = R.string.searchAutoRetryHint)
    )

    RetryView(
        isVisible = uiState.isRetry,
        errorMessage = uiState.retryMsg,
        icon = AppIcons.Warning
    ) { viewModel.onEvent(SearchUiEvent.Retry) }

    StartView(modifier, uiState)

    EmptyView(
        modifier = modifier,
        isVisible = uiState.isEmpty,
        icon = AppIcons.Search,
        message = buildNoResultHint(uiState.query)
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
            isVisible = uiState.isLoaded,
            result = uiState.result,
            favoriteRates = uiState.favoriteRates,
            navigateToDetail = { id -> navController.navigateToDetail(id) }
        ) { rate -> viewModel.onEvent(SearchUiEvent.OnFavorite(rate)) }
    }

}

@Composable
private fun buildNoResultHint(query: String) = buildAnnotatedString {
    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
        append(stringResource(id = R.string.noItemFound))
    }
    withStyle(
        style = SpanStyle(
            color = Color.Green,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
    ) {
        append(" $query")
    }
}

@Composable
private fun StartView(
    modifier: Modifier,
    uiState: SearchUiState
) {
    EmptyView(
        modifier = modifier,
        isVisible = uiState.isStart,
        icon = AppIcons.Search,
        message = stringResource(id = R.string.startSearchHint)
    )
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun HandleKeyboard(isKeyboardHidden: Boolean) {
    val keyboardController = LocalSoftwareKeyboardController.current
    if (isKeyboardHidden) {
        keyboardController?.hide()
    }
}

@Composable
private fun ContentView(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    result: List<ExchangeRate>,
    favoriteRates: List<ExchangeRate>,
    navigateToDetail: (id: String) -> Unit,
    onFavoriteClick: (rate: ExchangeRate) -> Unit
) {
    val lazyListState = rememberLazyListState()
    if (lazyListState.isScrollInProgress) {
        HandleKeyboard(isKeyboardHidden = true)
    }

    BaseLazyColumn(
        modifier = modifier,
        lazyListState = lazyListState,
        isVisible = isVisible,
        items = result,
    ) { rate ->
        val leadingIcon =
            if (favoriteRates.any { it.id == rate.id && it.symbol == rate.symbol }) {
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
