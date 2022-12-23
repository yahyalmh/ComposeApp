package com.example.favorite.di

import com.example.favorite.FavoriteRatesInteractor
import com.example.favorite.FavoriteRatesInteractorImpl
import com.example.favorite.FavoriteRepository
import com.example.favorite.FavoriteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FavoriteModule {

    @Binds
    fun bindFavoriteRatesRepository(repository: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    fun bindFavoriteRatesInteractor(favoriteRatesInteractor: FavoriteRatesInteractorImpl): FavoriteRatesInteractor
}