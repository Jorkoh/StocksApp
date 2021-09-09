package com.example.stocksapp.data.model.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class SymbolResponse(
    // https://iexcloud.io/docs/api/#symbols
    @Json(name = "symbol") val symbol: String,
    @Json(name = "name") val name: String,
    @Json(name = "date") val creationDate: LocalDate,
    @Json(name = "type") val type: String,
    @Json(name = "region") val region: String,
    @Json(name = "currency") val currency: String
)