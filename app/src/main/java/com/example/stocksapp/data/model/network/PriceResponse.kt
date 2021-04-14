package com.example.stocksapp.data.model.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PriceResponse(
    @Json(name = "date") val date: String,
    @Json(name = "close") val close: Double,
    @Json(name = "volume") val volume: Long?,
    @Json(name = "change") val change: Double?,
    @Json(name = "changePercent") val changePercent: Double?,
    @Json(name = "changeOverTime") val changeOverTime: Double?,
)
