package com.example.stocksapp.data.model.utils

import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.model.News
import com.example.stocksapp.data.model.Price
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.data.model.network.CompanyInfoResponse
import com.example.stocksapp.data.model.network.NewsResponse
import com.example.stocksapp.data.model.network.PriceResponse
import com.example.stocksapp.data.model.network.QuoteResponse
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ApiSuccessModelMapper
import java.time.Instant

object SuccessQuotesMapper : ApiSuccessModelMapper<List<QuoteResponse>, List<Quote>> {
    override fun map(apiErrorResponse: ApiResponse.Success<List<QuoteResponse>>): List<Quote> {
        val timestamp = Instant.now()
        return apiErrorResponse.data.map { it.mapToQuote(timestamp, true) }
    }

    private fun QuoteResponse.mapToQuote(timestamp: Instant, isTopActive: Boolean = false) = Quote(
        symbol = symbol,
        companyName = companyName,
        primaryExchange = primaryExchange,
        openPrice = open,
        openTime = openTime,
        closePrice = close,
        closeTime = closeTime,
        highPrice = high,
        highTime = highTime,
        lowPrice = low,
        lowTime = lowTime,
        latestPrice = latestPrice,
        latestSource = latestSource,
        latestTime = latestUpdate,
        latestVolume = latestVolume,
        extendedPrice = extendedPrice,
        extendedChange = extendedChange,
        extendedChangePercent = extendedChangePercent,
        extendedPriceTime = extendedPriceTime,
        previousClose = previousClose,
        previousVolume = previousVolume,
        change = change,
        changePercent = changePercent,
        volume = volume,
        avgTotalVolume = avgTotalVolume,
        marketCap = marketCap,
        peRatio = peRatio,
        week52High = week52High,
        week52Low = week52Low,
        ytdChange = ytdChange,
        lastTradeTime = lastTradeTime,
        isUSMarketOpen = isUSMarketOpen,
        isTopActive = isTopActive,
        fetchTimestamp = timestamp
    )
}

fun PriceResponse.mapToPrice(symbol: String, timestamp: Instant) = Price(
    symbol = symbol,
    date = date,
    closePrice = close,
    volume = volume,
    change = change,
    changePercent = changePercent,
    changeOverTime = changeOverTime,
    noDataDay = false,
    earliestAvailable = false,
    fetchTimestamp = timestamp
)

object SuccessCompanyInfoMapper : ApiSuccessModelMapper<CompanyInfoResponse, CompanyInfo> {
    override fun map(apiErrorResponse: ApiResponse.Success<CompanyInfoResponse>): CompanyInfo {
        return apiErrorResponse.data.mapToCompanyInfo(Instant.now())
    }

    private fun CompanyInfoResponse.mapToCompanyInfo(timestamp: Instant) = CompanyInfo(
        symbol = symbol,
        companyName = companyName,
        exchange = exchange,
        industry = industry,
        website = website,
        description = description,
        CEO = CEO,
        securityName = securityName,
        sector = sector,
        employees = employees,
        address = address,
        state = state,
        city = city,
        zip = zip,
        country = country,
        fetchTimestamp = timestamp
    )
}

object SuccessNewsMapper : ApiSuccessModelMapper<List<NewsResponse>, List<News>> {
    override fun map(apiErrorResponse: ApiResponse.Success<List<NewsResponse>>): List<News> {
        val timestamp = Instant.now()
        return apiErrorResponse.data.map { it.mapToNews(timestamp) }
    }

    private fun NewsResponse.mapToNews(timestamp: Instant) = News(
        date = datetime,
        headline = headline,
        source = source,
        url = url,
        summary = summary,
        symbols = related.split(','),
        imageUrl = image,
        fetchTimestamp = timestamp
    )
}
