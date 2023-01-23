package com.example.rate

import com.example.ui.common.ext.repeatFlow
import com.example.data.common.model.ExchangeDetailRate
import com.example.data.common.model.ExchangeRate
import com.example.rate.model.toExternalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * @author yaya (@yahyalmh)
 * @since 04th November 2022
 */

interface ExchangeRateInteractor {
    fun getRates(): Flow<List<ExchangeRate>>
    fun getLiveRates(interval: Long): Flow<List<ExchangeRate>>
    fun getLiveRate(id: String, interval: Long = 3000L): Flow<ExchangeDetailRate>
}

class ExchangeRateInteractorImpl @Inject constructor(private val exchangeRateRepository: ExchangeRateRepository) :
    ExchangeRateInteractor {

    override fun getRates() = flow { emit(exchangeRates()) }.flowOn(Dispatchers.IO)

    override fun getLiveRates(interval: Long) =
        repeatFlow(interval) { exchangeRates() }.flowOn(Dispatchers.IO)

    override fun getLiveRate(id: String, interval: Long): Flow<ExchangeDetailRate> =
        repeatFlow(interval) {
            exchangeRateRepository.getExchangeRate(id).toExternalModel()
        }.flowOn(Dispatchers.IO)

    private suspend fun exchangeRates() = exchangeRateRepository.getExchangeRates()
        .toExternalModel()
        .sortedByDescending { it.symbol }
}