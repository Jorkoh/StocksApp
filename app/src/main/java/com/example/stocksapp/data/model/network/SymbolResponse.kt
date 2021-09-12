package com.example.stocksapp.data.model.network

import androidx.annotation.StringRes
import com.example.stocksapp.R
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class SymbolResponse(
    // https://iexcloud.io/docs/api/#symbols
    @Json(name = "symbol") val symbol: String,
    @Json(name = "name") val name: String,
    @Json(name = "date") val creationDate: LocalDate,
    @Json(name = "type") val type: SymbolType,
    @Json(name = "region") val region: String,
    @Json(name = "currency") val currency: String
)

enum class SymbolType(@StringRes val uiStringResource: Int) {
    @Json(name = "ad") ADR(R.string.ad_symbol_type_name),
    @Json(name = "cs") CommonStock(R.string.cs_symbol_type_name),
    @Json(name = "cef") ClosedEndFund(R.string.cef_symbol_type_name),
    @Json(name = "et") ETF(R.string.et_symbol_type_name),
    @Json(name = "oef") OpenEndedFund(R.string.oef_symbol_type_name),
    @Json(name = "ps") PreferredStock(R.string.ps_symbol_type_name),
    @Json(name = "rt") Right(R.string.rt_symbol_type_name),
    @Json(name = "struct") StructuredProduct(R.string.struct_symbol_type_name),
    @Json(name = "ut") Unit(R.string.ut_symbol_type_name),
    @Json(name = "wi") WhenIssued(R.string.wi_symbol_type_name),
    @Json(name = "wt") Warrant(R.string.wt_symbol_type_name),
    Unknown(R.string.unknown_symbol_type_name),
}