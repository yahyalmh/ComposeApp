package com.example.data.common.api.di

import com.example.data.common.BuildConfig
import com.example.data.common.api.CoinCapRetrofit
import com.example.data.common.api.RetrofitBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @author yaya (@yahyalmh)
 * @since 04th November 2022
 */

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {
    @Binds
    fun bindCoincapRetrofit(coincapRetrofit: CoinCapRetrofit): RetrofitBuilder

    companion object {
        @Provides
        fun provideHttpClient(): OkHttpClient = if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()
        } else OkHttpClient.Builder().build()

        @Provides
        @Singleton
        fun provideBaseRetrofit(
            httpClient: OkHttpClient,
        ): Retrofit.Builder = Retrofit
            .Builder()
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
    }
}