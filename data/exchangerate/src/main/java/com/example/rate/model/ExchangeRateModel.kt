package com.example.rate.model

import com.example.common.model.ExchangeDetailRate
import com.example.common.model.ExchangeRate
import com.google.gson.annotations.SerializedName
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

fun ExchangeRateDetailModel.toExternalModel() = ExchangeDetailRate(
    id = rateDetail.id,
    symbol = rateDetail.symbol,
    currencySymbol = rateDetail.currencySymbol,
    type = rateDetail.type,
    rateUsd = BigDecimal(rateDetail.rateUsd),
    timestamp = timestamp.toLong()
)

data class ExchangeRatesModel(
    @SerializedName("data")
    val rates: List<ExchangeRateModel>
)

data class ExchangeRateDetailModel(
    @SerializedName("data")
    val rateDetail: ExchangeRateModel,
    val timestamp: String
)