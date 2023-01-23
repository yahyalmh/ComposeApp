package com.example.rate.model

import com.example.data.common.model.ExchangeDetailRate
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ExchangeRateDetailModel(
    @SerializedName("data")
    val rateDetail: ExchangeRateModel,
    val timestamp: String
)

fun ExchangeRateDetailModel.toExternalModel() = ExchangeDetailRate(
    id = rateDetail.id,
    symbol = rateDetail.symbol,
    currencySymbol = rateDetail.currencySymbol,
    type = rateDetail.type,
    rateUsd = BigDecimal(rateDetail.rateUsd),
    timestamp = timestamp.toLong()
)