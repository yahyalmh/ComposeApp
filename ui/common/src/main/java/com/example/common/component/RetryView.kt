package com.example.common.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.component.icon.AppIcons
import com.example.ui.common.R


/**
 * @author yaya (@yahyalmh)
 * @since 10th November 2022
 */

@Composable
fun RetryView(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    errorMessage: String,
    icon: ImageVector, onRetry: () -> Unit
) {
    AnimatedVisibility(visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()) {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = modifier
                    .padding(12.dp)
                    .size(80.dp),
                imageVector = icon,
                contentDescription = "retry icon",
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = modifier.height(10.dp))


            Text(
                text = errorMessage,
                modifier = modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = modifier.height(10.dp))

            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RetryPreview() {
    RetryView(icon = AppIcons.Warning, errorMessage = "This is a error message") {}
}
