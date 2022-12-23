package com.example.core.component

import androidx.compose.foundation.background
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * @author yaya (@yahyalmh)
 * @since 10th November 2022
 */

@Composable
fun LoadingView(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        BaseCenterColumn(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
        )
        { CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground) }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    LoadingView(isLoading = true)
}
