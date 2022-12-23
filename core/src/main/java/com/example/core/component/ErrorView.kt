package com.example.core.component

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
import com.example.core.R
import com.example.core.component.icon.Icons

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
                tint = MaterialTheme.colorScheme.errorContainer
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
                Text(text = stringResource(R.string.txt_retry))
            }
        }
    }
}


@Composable
fun AutoRetryView(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    errorMessage: String,
    icon: ImageVector,
    hint: String,
) {
    AnimatedVisibility(
        visible = isVisible, enter = fadeIn(), exit = fadeOut()
    ) {
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
                contentDescription = "Auto retry icon",
                tint = MaterialTheme.colorScheme.errorContainer
            )

            Text(
                text = errorMessage,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = modifier.height(10.dp))

            Text(
                text = hint,
                modifier = modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RetryPreview() {
    RetryView(icon = Icons.Warning, errorMessage = "This is a error message") {}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AutoRetryPreview() {
    AutoRetryView(
        icon = Icons.Warning, errorMessage = "This is a error message", hint = "Go online to try again"
    )
}
