package com.example.stocksapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@Entity(tableName = "quotes")
@JsonClass(generateAdapter = true)
data class Quote(
    @PrimaryKey
    val symbol: String,
    val companyName: String,
    val primaryExchange: String,

    val openPrice: Double,
    val openTime: Long,
    val closePrice: Double,
    val closeTime: Long,
    val highPrice: Double,
    val highTime: Long,
    val lowPrice: Double,
    val lowTime: Long,

    val latestPrice: Double,
    val latestSource: String,
    val latestTime: Long,
    val latestVolume: Long,

    val extendedPrice: Double,
    val extendedChange: Double,
    val extendedChangePercent: Double,
    val extendedPriceTime: Long,

    val previousClose: Double,
    val previousVolume: Long,

    val change: Double,
    val changePercent: Double,
    val volume: Long,

    val avgTotalVolume: Long,
    val marketCap: Long,
    val peRatio: Double,
    val week52High: Double,
    val week52Low: Double,
    val ytdChange: Double,
    val lastTradeTime: Long,
    val isUSMarketOpen: Boolean,

    val isTopActive: Boolean,
    val timestamp: Long = Date().time
)