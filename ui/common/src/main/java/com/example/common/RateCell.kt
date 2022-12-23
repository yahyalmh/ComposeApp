package com.example.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RateCell(
    rate: String,
    symbol: String,
    currencySymbol: String,
    type: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    Card(modifier = modifier
        .clickable { onClick() }
        .padding(8.dp))
    { Content(modifier, currencySymbol, rate, symbol, type, leadingIcon) }
}

@Composable
private fun Content(
    modifier: Modifier,
    currencySymbol: String,
    rate: String,
    symbol: String,
    type: String,
    leadingIcon: @Composable (() -> Unit)?
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(12.dp)
            .wrapContentSize()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSecondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = currencySymbol,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Column(modifier = modifier.padding(12.dp)) {
            Text(
                text = rate,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = modifier.height(10.dp))

            Text(
                text = symbol,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = modifier.height(10.dp))

            Text(
                text = type,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (leadingIcon != null) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides Color.Red, content = leadingIcon
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(heightDp = 150)
@Composable
fun RateCellPreview() {
    RateCell(
        rate = "3.343234342",
        symbol = "$$",
        currencySymbol = "USD",
        type = "Fiat",
        onClick = {},
        leadingIcon = {
            Icon(
                Icons.Filled.FavoriteBorder,
                contentDescription = "Check Icon",
                Modifier.size(AssistChipDefaults.IconSize)
            )
        }
    )
}
