package com.example.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.model.ExchangeRate
import com.example.core.component.icon.AppIcons
import java.util.*

@Composable
fun RateCell(
    modifier: Modifier = Modifier,
    rate: ExchangeRate,
    onClick: () -> Unit,
    leadingIcon: ImageVector? = null,
    onLeadingIconClick: (rate: ExchangeRate) -> Unit
) {
    Card(modifier = modifier
        .clickable { onClick() }
        .padding(8.dp),
        shape = RoundedCornerShape(12.dp))
    {
        Content(
            modifier = modifier,
            rate = rate,
            leadingIcon = leadingIcon,
            onLeadingIconClick = onLeadingIconClick
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    rate: ExchangeRate,
    leadingIcon: ImageVector?,
    onLeadingIconClick: (rate: ExchangeRate) -> Unit
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = modifier
                .size(74.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSecondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = rate.currencySymbol ?: rate.symbol,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Column(
            modifier = modifier.padding(start = 12.dp, 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Rate: ${rate.rateUsd.toString()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Symbol: ${rate.symbol}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Type: ${
                    rate.type.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                        else it.toString()
                    }
                }",
                        style = MaterialTheme . typography . bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        if (leadingIcon != null) {
            Column {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onLeadingIconClick(rate) },
                    imageVector = leadingIcon,
                    contentDescription = "Leading Icon",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(heightDp = 200, widthDp = 400)
@Composable
fun RateCellPreview() {
    RateCell(
        rate = ExchangeRate(
            id = "1",
            rateUsd = 3.343234342.toBigDecimal(),
            symbol = "$",
            currencySymbol = "USD",
            type = "Fiat"
        ),
        onClick = {},
        leadingIcon = AppIcons.Favorite
    ) {}
}
