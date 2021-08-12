package com.example.stocksapp.data.model.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant

// TODO times/dates as Date?
@JsonClass(generateAdapter = true)
data class QuoteResponse(
    // https://iexcloud.io/docs/api/#quote
    @Json(name = "symbol") val symbol: String,
    @Json(name = "companyName") val companyName: String,
    @Json(name = "primaryExchange") val primaryExchange: String,
    @Json(name = "calculationPrice") val calculationPrice: String,
    @Json(name = "open") val `open`: Double?,
    @Json(name = "openTime") val openTime: Instant?,
    @Json(name = "openSource") val openSource: String,
    @Json(name = "close") val close: Double?,
    @Json(name = "closeTime") val closeTime: Instant?,
    @Json(name = "closeSource") val closeSource: String?,
    @Json(name = "high") val high: Double?,
    @Json(name = "highTime") val highTime: Instant?,
    @Json(name = "highSource") val highSource: String?,
    @Json(name = "low") val low: Double?,
    @Json(name = "lowTime") val lowTime: Instant?,
    @Json(name = "lowSource") val lowSource: String?,
    @Json(name = "latestPrice") val latestPrice: Double,
    @Json(name = "latestSource") val latestSource: String,
    @Json(name = "latestTime") val latestTime: String,
    @Json(name = "latestUpdate") val latestUpdate: Instant,
    @Json(name = "latestVolume") val latestVolume: Long?,
    @Json(name = "delayedPrice") val delayedPrice: Double?,
    @Json(name = "delayedPriceTime") val delayedPriceTime: Instant?,
    @Json(name = "oddLotDelayedPrice") val oddLotDelayedPrice: Double?,
    @Json(name = "oddLotDelayedPriceTime") val oddLotDelayedPriceTime: Instant?,
    @Json(name = "extendedPrice") val extendedPrice: Double?,
    @Json(name = "extendedChange") val extendedChange: Double?,
    @Json(name = "extendedChangePercent") val extendedChangePercent: Double?,
    @Json(name = "extendedPriceTime") val extendedPriceTime: Instant?,
    @Json(name = "previousClose") val previousClose: Double,
    @Json(name = "previousVolume") val previousVolume: Long,
    @Json(name = "change") val change: Double,
    @Json(name = "changePercent") val changePercent: Double,
    @Json(name = "volume") val volume: Long?,
    @Json(name = "avgTotalVolume") val avgTotalVolume: Long,
    @Json(name = "marketCap") val marketCap: Long,
    @Json(name = "peRatio") val peRatio: Double?,
    @Json(name = "week52High") val week52High: Double,
    @Json(name = "week52Low") val week52Low: Double,
    @Json(name = "ytdChange") val ytdChange: Double,
    @Json(name = "lastTradeTime") val lastTradeTime: Instant,
    @Json(name = "isUSMarketOpen") val isUSMarketOpen: Boolean
)
