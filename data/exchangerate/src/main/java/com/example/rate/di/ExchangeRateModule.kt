package com.example.rate.di

import com.example.data.common.api.CoinCapRetrofit
import com.example.rate.api.ExchangeRateApi
import com.example.rate.ExchangeRateInteractor
import com.example.rate.ExchangeRateInteractorImpl
import com.example.rate.ExchangeRateRepository
import com.example.rate.ExchangeRateRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author yaya (@yahyalmh)
 * @since 02th November 2022
 */
@Module
@InstallIn(SingletonComponent::class)
interface ExchangeRateModule {

    companion object {
        @Provides
        @Singleton
        fun provideExchangeRateApi(coinCapRetrofit: CoinCapRetrofit): ExchangeRateApi =
            coinCapRetrofit.create(ExchangeRateApi::class.java)
    }

    @Binds
    @Singleton
    fun bindExchangeRateRepository(repository: ExchangeRateRepositoryImpl): ExchangeRateRepository

    @Binds
    fun bindExchangeInteractor(exchangeRateInteractor: ExchangeRateInteractorImpl): ExchangeRateInteractor
}