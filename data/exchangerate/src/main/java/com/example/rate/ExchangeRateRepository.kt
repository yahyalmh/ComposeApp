package com.example.rate

import com.example.rate.api.ExchangeRateApi
import com.example.rate.model.ExchangeRateDetailModel
import com.example.rate.model.ExchangeRatesModel
import javax.inject.Inject

/**
 * @author yaya (@yahyalmh)
 * @since 04th November 2022
 */

interface ExchangeRateRepository {
    suspend fun getExchangeRates(): ExchangeRatesModel
    suspend fun getExchangeRate(id: String): ExchangeRateDetailModel
}

class ExchangeRateRepositoryImpl @Inject constructor(private val exchangeRateApi: ExchangeRateApi) :
    ExchangeRateRepository {

    override suspend fun getExchangeRates(): ExchangeRatesModel =
        exchangeRateApi.getExchangeRates()

    override suspend fun getExchangeRate(id: String): ExchangeRateDetailModel {
        return exchangeRateApi.getExchangeRate(id)
    }
}