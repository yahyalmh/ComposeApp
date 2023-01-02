package com.example.search

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.data.common.model.ExchangeRate
import com.example.detail.nav.navigateToDetail
import com.example.ui.common.component.*
import com.example.ui.common.component.cell.RateShimmerCell
import com.example.ui.common.component.cell.toCell
import com.example.ui.common.component.icon.AppIcons
import com.example.ui.common.component.screen.SearchBarScaffold
import com.example.ui.common.component.view.AutoRetryView
import com.example.ui.common.component.view.EmptyView
import com.example.ui.common.component.view.RetryView
import com.example.ui.common.component.view.ShimmerView
import com.example.ui.common.ext.create
import com.example.ui.search.R

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    SearchViewContent(
        modifier = modifier,
        uiState = viewModel.state.value,
        navController = navController,
        onRetry = { viewModel.onEvent(SearchUiEvent.Retry) },
        onQueryChange = { query -> viewModel.onEvent(SearchUiEvent.QueryChange(query)) },
        onCancelClick = {
            viewModel.onEvent(SearchUiEvent.NavigationBack)
            navController.popBackStack()
        },
        onFavoriteClick = { rate -> viewModel.onEvent(SearchUiEvent.OnFavorite(rate)) }
    )
}

@Composable
private fun SearchViewContent(
    modifier: Modifier = Modifier,
    uiState: SearchUiState,
    navController: NavController,
    onRetry: () -> Unit,
    onCancelClick: () -> Unit,
    onQueryChange: (query: String) -> Unit,
    onFavoriteClick: (rate: ExchangeRate) -> Unit
) {
    HandleKeyboard(uiState.isKeyboardHidden)
    SearchBarScaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        hint = stringResource(id = R.string.searchBarHint),
        onQueryChange = onQueryChange,
        onCancelClick = onCancelClick
    ) {
        SearchShimmerView(
            modifier = modifier,
            isVisible = uiState.isLoading
        )
        AutoRetryView(
            isVisible = uiState.isAutoRetry,
            errorMessage = uiState.autoRetryMsg,
            icon = AppIcons.Warning,
            hint = stringResource(id = R.string.searchAutoRetryHint)
        )

        RetryView(
            isVisible = uiState.isRetry,
            retryMessage = uiState.retryMsg,
            icon = AppIcons.Warning, onRetry = onRetry
        )

        StartView(
            modifier = modifier,
            uiState = uiState
        )

        EmptyView(
            modifier = modifier,
            isVisible = uiState.isEmpty,
            icon = AppIcons.Search,
            message = buildNoResultHint(uiState.query)
        )

        DataView(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize(),
            isVisible = uiState.isLoaded,
            result = uiState.result,
            favoriteRates = uiState.favoriteRates,
            navigateToDetail = { id -> navController.navigateToDetail(id) },
            onFavoriteClick = onFavoriteClick,
        )
    }
}

@Composable
private fun DataView(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    result: List<ExchangeRate>,
    favoriteRates: List<ExchangeRate>,
    navigateToDetail: (id: String) -> Unit,
    onFavoriteClick: (rate: ExchangeRate) -> Unit
) {
    val models = result.map {
        it.toCell(
            favoritesRates = favoriteRates,
            navigateToDetail = navigateToDetail,
            onFavoriteClick = onFavoriteClick
        )
    }

    val lazyListState = rememberLazyListState()
    if (lazyListState.isScrollInProgress) {
        HandleKeyboard(isKeyboardHidden = true)
    }
    BaseLazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        lazyListState = lazyListState,
        isVisible = isVisible,
        models = models
    )
}

@Composable
fun SearchShimmerView(
    modifier: Modifier,
    isVisible: Boolean
) {
    ShimmerView(
        modifier = modifier,
        isVisible = isVisible,
    ) { shimmerAxis ->
        BaseLazyColumn(
            isVisible = true,
            models = create(count = 7) { { RateShimmerCell(shimmerAxis = shimmerAxis) } }
        )
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
@OptIn(ExperimentalComposeUiApi::class)
private fun HandleKeyboard(isKeyboardHidden: Boolean) {
    val keyboardController = LocalSoftwareKeyboardController.current
    if (isKeyboardHidden) {
        keyboardController?.hide()
    }
}

@Preview
@Composable
fun SearchPreview() {
    SearchViewContent(
        uiState = SearchUiState.Loading(""),
        navController = rememberNavController(),
        onRetry = {},
        onCancelClick = {},
        onQueryChange = {},
        onFavoriteClick = {}
    )
}
