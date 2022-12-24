package com.example.rate

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
    symbol = Random.nextString(),
    currencySymbol = Random.nextString(),
    type = Random.nextString(),
    rateUsd = Random.nextFloat().toString()
)

fun exchangeRateDetailModelStub() = ExchangeRateDetailModel(
    rateDetail = exchangeRateModelStub(),
    timestamp = Random.nextLong().toString()
)

fun Random.nextString(length: Int = 10, withNumbers: Boolean = true): String {
    val charsSet = (('A'..'Z') + ('a'..'z')).toMutableList()
    if (withNumbers) {
        charsSet += ('0'..'9')
    }
    return (1..length)
        .map { charsSet.random() }
        .joinToString("")
}