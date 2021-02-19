package com.example.stocksapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "most_active_symbols")
data class MostActiveSymbols(
    val symbols: List<String>,
    val timestamp: Long = Date().time,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)