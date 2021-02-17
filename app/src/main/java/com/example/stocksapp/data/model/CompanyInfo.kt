package com.example.stocksapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "company_infos")
@JsonClass(generateAdapter = true)
data class CompanyInfo(
    @PrimaryKey
    @Json(name = "symbol") val symbol: String,
    @Json(name = "companyName") val companyName: String,
    @Json(name = "exchange") val exchange: String,
    @Json(name = "industry") val industry: String,
    @Json(name = "website") val website: String,
    @Json(name = "description") val description: String,
    @Json(name = "CEO") val CEO: String,
    @Json(name = "securityName") val securityName: String,
    @Json(name = "issueType") val issueType: String,
    @Json(name = "sector") val sector: String,
    @Json(name = "primarySicCode") val primarySicCode: Int,
    @Json(name = "employees") val employees: Int?,
    @Json(name = "tags") val tags: List<String>,
    @Json(name = "address") val address: String,
    @Json(name = "address2") val address2: String?,
    @Json(name = "state") val state: String,
    @Json(name = "city") val city: String,
    @Json(name = "zip") val zip: String,
    @Json(name = "country") val country: String,
    @Json(name = "phone") val phone: String
)