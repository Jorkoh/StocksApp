package com.example.stocksapp.data.model.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuoteResponse(
    // https://iexcloud.io/docs/api/#quote
    @Json(name = "symbol") val symbol: String,
    @Json(name = "companyName") val companyName: String?,
    @Json(name = "primaryExchange") val primaryExchange: String?,
    @Json(name = "calculationPrice") val calculationPrice: String?,
    @Json(name = "open") val `open`: Double?,
    @Json(name = "openTime") val openTime: Long?,
    @Json(name = "openSource") val openSource: String?,
    @Json(name = "close") val close: Double?,
    @Json(name = "closeTime") val closeTime: Long?,
    @Json(name = "closeSource") val closeSource: String?,
    @Json(name = "high") val high: Double?,
    @Json(name = "highTime") val highTime: Long?,
    @Json(name = "highSource") val highSource: String?,
    @Json(name = "low") val low: Double?,
    @Json(name = "lowTime") val lowTime: Long?,
    @Json(name = "lowSource") val lowSource: String?,
    @Json(name = "latestPrice") val latestPrice: Double?,
    @Json(name = "latestSource") val latestSource: String?,
    @Json(name = "latestTime") val latestTime: String?,
    @Json(name = "latestUpdate") val latestUpdate: Long?,
    @Json(name = "latestVolume") val latestVolume: Long?,
    @Json(name = "iexRealtimePrice") val iexRealtimePrice: Double,
    @Json(name = "iexRealtimeSize") val iexRealtimeSize: Long?,
    @Json(name = "iexLastUpdated") val iexLastUpdated: Long?,
    @Json(name = "delayedPrice") val delayedPrice: Double?,
    @Json(name = "delayedPriceTime") val delayedPriceTime: Long?,
    @Json(name = "oddLotDelayedPrice") val oddLotDelayedPrice: Double?,
    @Json(name = "oddLotDelayedPriceTime") val oddLotDelayedPriceTime: Long?,
    @Json(name = "extendedPrice") val extendedPrice: Double?,
    @Json(name = "extendedChange") val extendedChange: Double?,
    @Json(name = "extendedChangePercent") val extendedChangePercent: Double?,
    @Json(name = "extendedPriceTime") val extendedPriceTime: Long?,
    @Json(name = "previousClose") val previousClose: Double?,
    @Json(name = "previousVolume") val previousVolume: Long?,
    @Json(name = "change") val change: Double?,
    @Json(name = "changePercent") val changePercent: Double?,
    @Json(name = "volume") val volume: Long?,
    @Json(name = "iexMarketPercent") val iexMarketPercent: Double?,
    @Json(name = "iexVolume") val iexVolume: Long?,
    @Json(name = "avgTotalVolume") val avgTotalVolume: Long?,
    @Json(name = "iexBidPrice") val iexBidPrice: Double?,
    @Json(name = "iexBidSize") val iexBidSize: Long?,
    @Json(name = "iexAskPrice") val iexAskPrice: Double?,
    @Json(name = "iexAskSize") val iexAskSize: Long?,
    @Json(name = "iexOpen") val iexOpen: Double?,
    @Json(name = "iexOpenTime") val iexOpenTime: Long?,
    @Json(name = "iexClose") val iexClose: Double?,
    @Json(name = "iexCloseTime") val iexCloseTime: Long?,
    @Json(name = "marketCap") val marketCap: Long?,
    @Json(name = "peRatio") val peRatio: Double?,
    @Json(name = "week52High") val week52High: Double?,
    @Json(name = "week52Low") val week52Low: Double?,
    @Json(name = "ytdChange") val ytdChange: Double?,
    @Json(name = "lastTradeTime") val lastTradeTime: Long?,
    @Json(name = "isUSMarketOpen") val isUSMarketOpen: Boolean?
)