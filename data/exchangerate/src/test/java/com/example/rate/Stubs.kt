package com.example.rate

import com.example.data.common.ext.RandomString
import com.example.rate.model.ExchangeRateDetailModel
import com.example.rate.model.ExchangeRateModel
import com.example.rate.model.ExchangeRatesModel
import kotlin.random.Random

fun exchangeRatesModelStub() = ExchangeRatesModel(exchangeRatesStub())

fun exchangeRatesStub(count: Int = 10): List<ExchangeRateModel> {
    val result = mutableListOf<ExchangeRateModel>()
    repeat(count) {
        result.add(exchangeRateModelStub())
    }
    return result
}

fun exchangeRateModelStub() = ExchangeRateModel(
    id = Random.nextInt().toString(),
    symbol = RandomString.next(),
    currencySymbol = RandomString.next(),
    type = RandomString.next(),
    rateUsd = Random.nextFloat().toString()
)

fun exchangeRateDetailModelStub() = ExchangeRateDetailModel(
    rateDetail = exchangeRateModelStub(),
    timestamp = Random.nextLong().toString()
)