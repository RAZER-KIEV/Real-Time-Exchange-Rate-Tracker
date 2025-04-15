package com.example.real_timeexchangeratetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey
    val id: Long,
    val shortName: String,
    val longName: String,
    val lastKnownValue: Double,
    val lastUpdated: Long,
    val isCrypto: Boolean,
    val iconLink: String,
    val isMonitored: Boolean
)

