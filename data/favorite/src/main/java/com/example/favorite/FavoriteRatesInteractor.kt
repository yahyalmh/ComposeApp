package com.example.favorite

import com.example.common.database.ExchangeRateEntity
import com.example.common.model.ExchangeRate
import com.example.rate.ExchangeRateInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface FavoriteRatesInteractor {
    fun getLiveFavoriteRates(interval: Long = 4000L): Flow<List<ExchangeRate>>
    fun getFavoriteRates(): Flow<List<ExchangeRate>>
    fun getFavoriteRatesEntities(): Flow<List<ExchangeRateEntity>>
    suspend fun addFavorite(rate: ExchangeRate)
    suspend fun removeFavorite(rate: ExchangeRate)
}

class FavoriteRatesInteractorImpl @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val rateInteractor: ExchangeRateInteractor,
) : FavoriteRatesInteractor {

    override fun getLiveFavoriteRates(interval: Long): Flow<List<ExchangeRate>> {
        return combine(
            rateInteractor.getLiveRates(interval),
            favoriteRepository.getFavoriteRates()
        ) { rates, favoriteRates ->
            rates.filter { rate ->
                favoriteRates.any { it.id == rate.id && it.symbol == rate.symbol }
            }
        }
    }


    override fun getFavoriteRates(): Flow<List<ExchangeRate>> {
        return combine(
            rateInteractor.getRates(),
            favoriteRepository.getFavoriteRates()
        ) { rates, favoriteRates ->
            rates.filter { rate ->
                favoriteRates.any { it.id == rate.id && it.symbol == rate.symbol }
            }
        }
    }

    override fun getFavoriteRatesEntities(): Flow<List<ExchangeRateEntity>> =
        favoriteRepository.getFavoriteRates()

    override suspend fun addFavorite(rate: ExchangeRate) = favoriteRepository.addFavorite(rate)

    override suspend fun removeFavorite(rate: ExchangeRate) =
        favoriteRepository.removeFavorite(rate)
}