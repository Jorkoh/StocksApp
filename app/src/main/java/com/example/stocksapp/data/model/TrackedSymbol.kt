package com.example.stocksapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_symbols")
data class TrackedSymbol(
    @PrimaryKey
    val symbol: String
)