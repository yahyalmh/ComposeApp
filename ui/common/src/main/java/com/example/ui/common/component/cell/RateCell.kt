package com.example.ui.common.component.cell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.common.model.ExchangeRate
import com.example.ui.common.R
import com.example.ui.common.component.icon.AppIcons
import com.example.ui.common.component.view.ShimmerAxis
import com.example.ui.common.component.view.ShimmerGradient
import com.example.ui.common.component.view.shimmerBackground
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
        .height(120.dp)
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
            .padding(8.dp),
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
                text = "Rate: ${rate.rateUsd}",
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
                style = MaterialTheme.typography.bodyMedium,
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
                    contentDescription = stringResource(id = R.string.favoriteIconDescription),
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun RateShimmerCell(
    modifier: Modifier = Modifier,
    shimmerAxis: ShimmerAxis,
) {
    Card(
        modifier = modifier
            .width(400.dp)
            .height(120.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = modifier
                .shimmerBackground(
                    ShimmerGradient.Linear(
                        MaterialTheme.colorScheme.secondaryContainer,
                        shimmerAxis
                    )
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
            )
            Column(
                modifier = modifier.padding(start = 12.dp, 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = modifier
                        .size(150.dp, 12.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                )
                Box(
                    modifier = modifier
                        .size(100.dp, 10.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                )
                Box(
                    modifier = modifier
                        .size(70.dp, 10.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ExchangeRate.toCell(
    favoritesRates: List<ExchangeRate>,
    navigateToDetail: (id: String) -> Unit,
    onFavoriteClick: (rate: ExchangeRate) -> Unit
): @Composable () -> Unit = {
    val leadingIcon = if (favoritesRates.any { it.id == this.id && it.symbol == this.symbol }) {
        AppIcons.Favorite
    } else {
        AppIcons.FavoriteBorder
    }
    RateCell(
        rate = this,
        leadingIcon = leadingIcon,
        onClick = { navigateToDetail(this.id) },
        onLeadingIconClick = onFavoriteClick
    )
}

@Preview
@Composable
fun RateShimmerCellPreview() {
    RateShimmerCell(shimmerAxis = ShimmerAxis(200f, 200f, 400f, 400f))
}

@Preview
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
