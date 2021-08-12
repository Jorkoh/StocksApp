package com.example.stocksapp.data.repositories.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.time.LocalDate

class LocalDateAdapter : JsonAdapter<LocalDate>() {

    @FromJson
    override fun fromJson(reader: JsonReader): LocalDate? {
        return if (reader.peek() == JsonReader.Token.STRING) LocalDate.parse(reader.nextString()) else reader.nextNull()
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: LocalDate?) {
        throw UnsupportedOperationException()
    }
}