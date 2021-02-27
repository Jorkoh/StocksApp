package com.example.stocksapp.data.model.utils

import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.model.News
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.data.model.network.CompanyInfoResponse
import com.example.stocksapp.data.model.network.NewsResponse
import com.example.stocksapp.data.model.network.QuoteResponse

fun QuoteResponse.mapToQuote(timestamp: Long, isTopActive: Boolean = false) = Quote(
    symbol = symbol,
    companyName = companyName.orUnknown(),
    primaryExchange = primaryExchange.orUnknown(),
    openPrice = open.orUnknown(),
    openTime = openTime.orUnknown(),
    closePrice = close.orUnknown(),
    closeTime = closeTime.orUnknown(),
    highPrice = high.orUnknown(),
    highTime = highTime.orUnknown(),
    lowPrice = low.orUnknown(),
    lowTime = lowTime.orUnknown(),
    latestPrice = latestPrice.orUnknown(),
    latestSource = latestSource.orUnknown(),
    latestTime = latestUpdate.orUnknown(),
    latestVolume = latestVolume.orUnknown(),
    extendedPrice = extendedPrice.orUnknown(),
    extendedChange = extendedChange.orUnknown(),
    extendedChangePercent = extendedChangePercent.orUnknown(),
    extendedPriceTime = extendedPriceTime.orUnknown(),
    previousClose = previousClose.orUnknown(),
    previousVolume = previousVolume.orUnknown(),
    change = change.orUnknown(),
    changePercent = changePercent.orUnknown(),
    volume = volume.orUnknown(),
    avgTotalVolume = avgTotalVolume.orUnknown(),
    marketCap = marketCap.orUnknown(),
    peRatio = peRatio.orUnknown(),
    week52High = week52High.orUnknown(),
    week52Low = week52Low.orUnknown(),
    ytdChange = ytdChange.orUnknown(),
    lastTradeTime = lastTradeTime.orUnknown(),
    isUSMarketOpen = isUSMarketOpen.orUnknown(),
    isTopActive = isTopActive,
    timestamp = timestamp
)

fun CompanyInfoResponse.mapToCompanyInfo(timestamp: Long) = CompanyInfo(
    symbol = symbol,
    companyName = companyName.orUnknown(),
    exchange = exchange.orUnknown(),
    industry = industry.orUnknown(),
    website = website.orUnknown(),
    description = description.orUnknown(),
    CEO = CEO.orUnknown(),
    securityName = securityName.orUnknown(),
    sector = sector.orUnknown(),
    employees = employees.orUnknown(),
    address = "${address.orUnknown()}${if (address2 != null) "\n$address2" else ""}",
    state = state.orUnknown(),
    city = city.orUnknown(),
    zip = zip.orUnknown(),
    country = country.orUnknown(),
    timestamp = timestamp
)

fun NewsResponse.mapToNews(timestamp: Long) = News(
    date = datetime,
    headline = headline,
    source = source,
    url = url,
    summary = summary,
    symbols = related.split(','),
    imageUrl = image,
    hasPaywall = hasPaywall,
    timestamp = timestamp
)

private fun String?.orUnknown(): String = this ?: "-"
private fun Int?.orUnknown(): Int = this ?: -1
private fun Double?.orUnknown(): Double = this ?: -1.0
private fun Long?.orUnknown(): Long = this ?: -1
private fun Boolean?.orUnknown(): Boolean = this ?: false
