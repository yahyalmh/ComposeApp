package com.example.main.connectivity.di

import com.example.main.connectivity.ConnectivityMonitor
import com.example.main.connectivity.ConnectivityMonitorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @author yaya (@yahyalmh)
 * @since 04th November 2022
 */

@Module
@InstallIn(SingletonComponent::class)
interface ConnectivityMonitorModule {
    @Binds
    fun bindConnectivityMonitor(networkMonitor: ConnectivityMonitorImpl): ConnectivityMonitor
}