package com.example.stocksapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey
    val symbol: String,
    val companyName: String,
    val primaryExchange: String,

    val openPrice: Double?,
    val openTime: Instant?,
    val closePrice: Double?,
    val closeTime: Instant?,
    val highPrice: Double?,
    val highTime: Instant?,
    val lowPrice: Double?,
    val lowTime: Instant?,

    val latestPrice: Double,
    val latestSource: String,
    val latestTime: Instant,
    val latestVolume: Long?,

    val extendedPrice: Double?,
    val extendedChange: Double?,
    val extendedChangePercent: Double?,
    val extendedPriceTime: Instant?,

    val previousClose: Double,
    val previousVolume: Long,

    val change: Double,
    val changePercent: Double,
    val volume: Long?,

    val avgTotalVolume: Long,
    val marketCap: Long?,
    val peRatio: Double?,
    val week52High: Double,
    val week52Low: Double,
    val ytdChange: Double,
    val lastTradeTime: Instant,
    val isUSMarketOpen: Boolean,

    val isTopActive: Boolean,
    val fetchTimestamp: Instant
)
