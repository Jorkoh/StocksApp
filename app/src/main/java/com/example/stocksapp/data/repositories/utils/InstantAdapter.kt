package com.example.stocksapp.data.repositories.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.time.Instant

class InstantAdapter : JsonAdapter<Instant>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Instant? {
        return if (reader.peek() == JsonReader.Token.NUMBER) Instant.ofEpochMilli(reader.nextLong()) else reader.nextNull()
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Instant?) {
        throw UnsupportedOperationException()
    }
}