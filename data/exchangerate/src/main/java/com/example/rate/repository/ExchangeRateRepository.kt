package com.example.rate.repository

import com.example.rate.model.ExchangeRateDetailModel
import com.example.rate.model.ExchangeRatesModel

/**
 * @author yaya (@yahyalmh)
 * @since 04th November 2022
 */

interface ExchangeRateRepository {
    suspend fun getExchangeRates(): ExchangeRatesModel

    suspend fun getExchangeRate(id: String): ExchangeRateDetailModel
}