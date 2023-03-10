package com.example.rate.model

import com.example.data.common.model.ExchangeRate
import java.math.BigDecimal

/**
 * @author yaya (@yahyalmh)
 * @since 02th November 2022
 */

data class ExchangeRateModel(
    val id: String,
    val symbol: String,
    val currencySymbol: String?,
    val type: String,
    val rateUsd: String
)

fun ExchangeRateModel.toExternalModel() = ExchangeRate(
    id = id,
    symbol = symbol,
    currencySymbol = currencySymbol,
    type = type,
    rateUsd = BigDecimal(rateUsd)
)