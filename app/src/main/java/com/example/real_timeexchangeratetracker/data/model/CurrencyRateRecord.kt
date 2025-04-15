package com.example.real_timeexchangeratetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statuses")
data class CurrencyRateRecord(
    @PrimaryKey
    val id: Long,
    val currencyId: Long,
    val lastKnownValue: Double,
    val lastUpdated: Long,
)