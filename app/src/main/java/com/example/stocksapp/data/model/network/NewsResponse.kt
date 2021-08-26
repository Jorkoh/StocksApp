package com.example.stocksapp.data.model.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant

@JsonClass(generateAdapter = true)
data class NewsResponse(
    // https://iexcloud.io/docs/api/#news
    @Json(name = "datetime") val datetime: Instant,
    @Json(name = "headline") val headline: String,
    @Json(name = "source") val source: String,
    @Json(name = "url") val url: String,
    @Json(name = "summary") val summary: String,
    @Json(name = "related") val related: String,
    @Json(name = "image") val image: String,
    @Json(name = "lang") val lang: String,
)
