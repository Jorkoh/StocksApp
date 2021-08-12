package com.example.stocksapp.data.repositories.utils

import com.example.stocksapp.data.model.network.NewsResponse
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class BatchedNews

// TODO: this adapter is similar to [BatchedQuotesAdapter], can they be made generic?
class BatchedNewsAdapter {
    companion object {
        val adapter: JsonAdapter<NewsResponse> = Moshi.Builder()
            .add(InstantAdapter())
            .build()
            .adapter(NewsResponse::class.java)
    }

    @BatchedNews
    @FromJson
    fun fromJson(reader: JsonReader): List<NewsResponse> {
        val news = mutableListOf<NewsResponse>()
        reader.beginObject()
        while (reader.hasNext()) {
            reader.skipName()
            reader.beginObject()
            reader.skipName()
            reader.beginArray()
            while (reader.hasNext()) {
                adapter.fromJson(reader)?.let {
                    news.add(it)
                }
            }
            reader.endArray()
            reader.endObject()
        }
        reader.endObject()
        return news
    }

    @ToJson
    fun toJson(@BatchedNews value: List<NewsResponse>): String {
        throw UnsupportedOperationException()
    }
}
