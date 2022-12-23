package com.example.rate.interactor

import com.example.common.model.ExchangeDetailRate
import com.example.core.ext.repeatFlow
import com.example.rate.model.toExternalModel
import com.example.rate.repository.ExchangeRateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * @author yaya (@yahyalmh)
 * @since 04th November 2022
 */

class ExchangeRateInteractorImpl @Inject constructor(private val exchangeRateRepository: ExchangeRateRepository) :
    ExchangeRateInteractor {

    override fun getRates() = flow { emit(exchangeRates()) }.flowOn(Dispatchers.IO)

    override fun getLiveRates(interval: Long) =
        repeatFlow(interval) { exchangeRates() }.flowOn(Dispatchers.IO)

    override fun getLiveRate(id: String, interval: Long): Flow<ExchangeDetailRate> =
        repeatFlow(interval) { exchangeRateRepository.getExchangeRate(id).toExternalModel() }
            .flowOn(Dispatchers.IO)

    private suspend fun exchangeRates() = exchangeRateRepository.getExchangeRates()
        .rates.map { rate -> rate.toExternalModel() }
        .sortedByDescending { it.symbol }
}