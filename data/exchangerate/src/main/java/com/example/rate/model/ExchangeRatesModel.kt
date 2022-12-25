package com.example.rate.model

import com.example.common.model.ExchangeRate
import com.google.gson.annotations.SerializedName

data class ExchangeRatesModel(
    @SerializedName("data")
    val rates: List<ExchangeRateModel>
)

fun ExchangeRatesModel.toExternalModel(): List<ExchangeRate> =
    this.rates.map { it.toExternalModel() }
