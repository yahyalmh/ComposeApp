package com.example.favorite;

import com.example.data.common.database.ExchangeRateDao
import com.example.data.common.database.ExchangeRateEntity
import com.example.data.common.model.ExchangeRate
import com.example.data.common.model.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FavoriteRepository {
    suspend fun addFavorite(rate: ExchangeRate)
    suspend fun removeFavorite(rate: ExchangeRate)
    fun getFavoriteRates(): Flow<List<ExchangeRateEntity>>
}

class FavoriteRepositoryImpl @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao
) : FavoriteRepository {

    override fun getFavoriteRates(): Flow<List<ExchangeRateEntity>> {
        return exchangeRateDao.getAll()
    }

    override suspend fun addFavorite(rate: ExchangeRate) = exchangeRateDao.insert(rate.toEntity())

    override suspend fun removeFavorite(rate: ExchangeRate) =
        exchangeRateDao.delete(rate.toEntity())

}
