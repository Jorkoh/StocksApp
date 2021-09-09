package com.example.stocksapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "symbols")
data class Symbol(
    @PrimaryKey
    val symbol: String,
    val creationDate: LocalDate,
    val type: String,
    val region: String,
    val currency: String,

    val userTracked: Boolean,

    val fetchTimestamp: Instant
)