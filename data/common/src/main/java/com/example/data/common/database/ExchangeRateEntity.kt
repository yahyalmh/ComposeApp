package com.example.data.common.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_rates")
data class ExchangeRateEntity(
    @PrimaryKey val id: String,
    val symbol: String,
)