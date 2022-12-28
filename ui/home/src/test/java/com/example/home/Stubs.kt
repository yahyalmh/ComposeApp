package com.example.home

import com.example.common.ext.RandomString
import com.example.common.model.ExchangeRate
import kotlin.random.Random

fun exchangeRatesStub(count: Int = 10): List<ExchangeRate> {
    val result = mutableListOf<ExchangeRate>()
    repeat(count) {
        result.add(exchangeRateStub())
    }
    return result
}

fun exchangeRateStub() = ExchangeRate(
    id = Random.nextInt().toString(),
    symbol = RandomString.next(),
    currencySymbol = RandomString.next(),
    type = RandomString.next(),
    rateUsd = Random.nextLong().toBigDecimal()
)