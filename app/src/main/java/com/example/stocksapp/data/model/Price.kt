package com.example.stocksapp.data.model

import androidx.room.Entity
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "prices", primaryKeys = ["symbol", "date"])
data class Price(
    val symbol: String,
    val date: LocalDate,
    val closePrice: Double,
    val volume: Long,
    val change: Double,
    val changePercent: Double,
    val changeOverTime: Double,

    val noDataDay: Boolean,
    val earliestAvailable: Boolean, // support for "All" chart range
    val fetchTimestamp: Instant
)