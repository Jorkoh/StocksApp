package com.example.stocksapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company_infos")
data class CompanyInfo(
    @PrimaryKey
    val symbol: String,
    val companyName: String,
    val exchange: String,
    val industry: String,
    val website: String,
    val description: String,
    val CEO: String,
    val securityName: String,
    val sector: String,
    val employees: Int,
    val address: String,
    val state: String,
    val city: String,
    val zip: String,
    val country: String,
    val timestamp: Long
)