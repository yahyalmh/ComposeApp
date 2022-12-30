package com.example.ui.common.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> BaseLazyColumn(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    items: List<T>,
    isVisible: Boolean = true,
    cell: @Composable (T) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier
        ) {
            items(items) { cell(it) }
        }
    }
}

@Composable
fun BaseCenterColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseLazyColumn(
    modifier: Modifier,
    models: List<@Composable () -> Unit>,
    stickyHeader: @Composable (() -> Unit)? = null,
    lazyListState: LazyListState = rememberLazyListState(),
    isVisible: Boolean = true,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            stickyHeader?.let { stickyHeader { it() } }
            items(models){it()}
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseLazyColumn(
    modifier: Modifier,
    models: Map<@Composable () -> Unit, List<@Composable () -> Unit>>,
    lazyListState: LazyListState = rememberLazyListState(),
    isVisible: Boolean = true,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            models.forEach {(initial, ratesForInitial) ->
                 stickyHeader { initial() }
                items(ratesForInitial) { it() }
            }
        }
    }
}