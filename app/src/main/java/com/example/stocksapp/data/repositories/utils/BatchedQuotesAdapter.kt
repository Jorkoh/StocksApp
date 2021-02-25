package com.example.stocksapp.data.repositories.utils

import com.example.stocksapp.data.model.network.QuoteResponse
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class BatchedQuotes

class BatchedQuotesAdapter {
    companion object {
        val adapter: JsonAdapter<QuoteResponse> = Moshi.Builder().build().adapter(
            QuoteResponse::class.java
        )
    }

    @BatchedQuotes
    @FromJson
    fun fromJson(reader: JsonReader): List<QuoteResponse> {
        val responses = mutableListOf<QuoteResponse>()
        reader.beginObject()
        while (reader.hasNext()) {
            reader.skipName()
            reader.beginObject()
            reader.skipName()
            adapter.fromJson(reader)?.let {
                responses.add(it)
            }
            reader.endObject()
        }
        reader.endObject()
        return responses
    }

    @ToJson
    fun toJson(@BatchedQuotes value: List<QuoteResponse>): String {
        throw UnsupportedOperationException()
    }
}
