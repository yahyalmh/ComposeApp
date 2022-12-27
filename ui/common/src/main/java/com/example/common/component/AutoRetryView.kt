package com.example.common.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.component.icon.AppIcons
import com.example.ui.common.R

@Composable
fun AutoRetryView(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    errorMessage: String,
    icon: ImageVector,
    hint: String = stringResource(id = R.string.autoRetryHint),
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
                contentDescription = stringResource(id = R.string.autoRetryIconDescription),
                tint = MaterialTheme.colorScheme.error
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
                color = Color.Green
            )

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AutoRetryPreview() {
    AutoRetryView(
        icon = AppIcons.Warning,
        errorMessage = "This is a error message",
        hint = "Go online to try again"
    )
}