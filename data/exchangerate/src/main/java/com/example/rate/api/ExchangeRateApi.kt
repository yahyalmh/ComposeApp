package com.example.rate.api

import com.example.rate.model.ExchangeRateDetailModel
import com.example.rate.model.ExchangeRatesModel
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author yaya (@yahyalmh)
 * @since 02th November 2022
 */

interface ExchangeRateApi {

    @GET("v2/rates")
    suspend fun getExchangeRates(): ExchangeRatesModel

    @GET("v2/rates/{id}")
    suspend fun getExchangeRate(@Path("id") id: String): ExchangeRateDetailModel
}