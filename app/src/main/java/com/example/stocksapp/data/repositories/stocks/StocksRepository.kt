package com.example.stocksapp.data.repositories.stocks

import androidx.annotation.WorkerThread
import com.example.stocksapp.data.database.StocksDao
import com.example.stocksapp.data.model.Price
import com.example.stocksapp.data.model.utils.SuccessCompanyInfoMapper
import com.example.stocksapp.data.model.utils.SuccessNewsMapper
import com.example.stocksapp.data.model.utils.SuccessQuotesMapper
import com.example.stocksapp.data.model.utils.mapToPrice
import com.example.stocksapp.data.repositories.stocks.IEXService.ChartRanges.OneMonth
import com.example.stocksapp.data.repositories.stocks.IEXService.ChartRanges.OneWeek
import com.example.stocksapp.data.repositories.stocks.IEXService.ChartRanges.OneYear
import com.example.stocksapp.data.repositories.stocks.IEXService.ChartRanges.ThreeMonths
import com.example.stocksapp.ui.components.charts.line.LineChartData
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.random.Random

class StocksRepository @Inject constructor(
    private val IEXService: IEXService,
    private val stocksDao: StocksDao
) {

    @WorkerThread
    fun fetchIsTracked(
        symbol: String
    ) = stocksDao.symbolIsTracked(symbol)

    @WorkerThread
    suspend fun toggleIsTracked(
        symbol: String,
        isTracked: Boolean
    ) {
        stocksDao.updateIsTracked(symbol, isTracked)
    }

    @WorkerThread
    fun fetchTrackedSymbols(
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow<List<List<Price>>> {
        val trackedSymbols = stocksDao.getTrackedSymbols().first()
        if (trackedSymbols.isEmpty()) {
            emit(emptyList())
        } else {
            combine(trackedSymbols.map { trackedSymbol ->
                fetchChartPrices(
                    symbol = trackedSymbol.symbol,
                    range = OneWeek,
                    onStart = {},
                    onError = { onError(it) }
                )
            }) { it.toList() }.collect {
                emit(it)
            }
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun fetchChartPrices(
        symbol: String,
        range: IEXService.ChartRanges,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        val nowInEasternTime = Instant.now().atZone(ZoneId.of("America/New_York"))
        val lastDate = nowInEasternTime.toLocalDate().minusDays(
            // Trading day data becomes available after 4AM ET the next day
            if (nowInEasternTime.hour >= 4) {
                1
            } else {
                2
            }
        )
        val firstDate = lastDate.apply {
            when (range) {
                OneWeek -> minus(1, ChronoUnit.WEEKS)
                OneMonth -> minus(1, ChronoUnit.MONTHS)
                ThreeMonths -> minus(3, ChronoUnit.MONTHS)
                OneYear -> minus(1, ChronoUnit.YEARS)
            }
            plusDays(1)
        }
        val daysBetween = firstDate.until(lastDate).plusDays(1).days

        stocksDao.getChartPrices(
            symbol = symbol,
            firstDate = firstDate,
            lastDate = lastDate
        ).distinctUntilChanged().collect { cachedPrices ->
            if (cachedPrices.size != daysBetween) {
                // API fetch TODO: if the missing dates are all at the end only fetch that missing range
                IEXService.fetchChartPrices(symbol, range).suspendOnSuccess {
                    val timestamp = Instant.now()
                    val apiPrices = data.map { it.mapToPrice(symbol, timestamp) }
                    val dates = List(daysBetween) { offset -> firstDate.plusDays(offset.toLong()) }
                    val missingPrices = dates.toSet().minus(apiPrices.map { it.date }.toSet()).map { date ->
                        Price(
                            symbol = symbol,
                            date = date,
                            closePrice = 0.0,
                            volume = 0,
                            change = 0.0,
                            changePercent = 0.0,
                            changeOverTime = 0.0,
                            noDataDay = true,
                            earliestAvailable = false,
                            fetchTimestamp = timestamp
                        )
                    }
                    stocksDao.insertChartPrices(apiPrices + missingPrices)
                }.onError {
                    onError("Request failed with code ${statusCode.code}: $raw")
                }.onException {
                    onError("Error while requesting: $message")
                }
            } else {
                // DB cache
                emit(cachedPrices)
            }
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun fetchTopActiveQuotes(
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        stocksDao.getQuotesByActivity(
            isTopActive = true,
            timestampCutoff = Instant.now().minus(1, ChronoUnit.HOURS)
        ).distinctUntilChanged().collect { cachedQuotes -> // TODO why is distinctUntilChanged needed?
            if (cachedQuotes.isEmpty()) {
                // API fetch
                IEXService.fetchMostActiveSymbols().suspendOnSuccess(SuccessQuotesMapper) {
                    stocksDao.refreshTopActiveQuotes(this)
                }.onError {
                    onError("Request failed with code ${statusCode.code}: $raw")
                }.onException {
                    onError("Error while requesting: $message")
                }
            } else {
                // DB cache
                emit(cachedQuotes)
            }
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun fetchNews(
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        stocksDao.getNews(
            timestampCutoff = Instant.now().minus(2, ChronoUnit.HOURS)
        ).distinctUntilChanged().collect { cachedNews -> // TODO why is distinctUntilChanged needed?
            if (cachedNews.isEmpty()) {
                // API fetch
                val trackedSymbols = stocksDao.getTrackedSymbols().first()
                val newsSymbols = if (trackedSymbols.isNotEmpty()) {
                    // If the user has tracked symbols fetch news from those
                    trackedSymbols.joinToString(separator = ",") { it.symbol }
                } else {
                    // If the user doesn't have tracked symbols fetch news from most active
                    // TODO take care of error fetching top active quotes
                    val mostActiveSymbols = fetchTopActiveQuotes({}, {}).first()
                    mostActiveSymbols.joinToString(separator = ",") { it.symbol }
                }
                IEXService.fetchNews(newsSymbols).suspendOnSuccess(SuccessNewsMapper) {
                    stocksDao.refreshNews(this)
                }.onError {
                    onError("Request failed with code ${statusCode.code}: $raw")
                }.onException {
                    onError("Error while requesting: $message")
                }
            } else {
                // DB cache
                emit(cachedNews)
            }
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun fetchCompanyInfo(
        symbol: String,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        stocksDao.getCompanyInfo(
            symbol = symbol,
            timestampCutoff = Instant.now().minus(7, ChronoUnit.DAYS)
        ).distinctUntilChanged().collect { cachedCompanyInfo -> // TODO why is distinctUntilChanged needed?
            if (cachedCompanyInfo == null) {
                // API fetch
                IEXService.fetchCompanyInfo(symbol).suspendOnSuccess(SuccessCompanyInfoMapper) {
                    stocksDao.insertCompanyInfo(this)
                }.onError {
                    onError("Request failed with code ${statusCode.code}: $raw")
                }.onException {
                    onError("Error while requesting: $message")
                }
            } else {
                // DB cache
                emit(cachedCompanyInfo)
            }
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun fetchChartPrices(
        symbol: String,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        emit(
            LineChartData(
                (1..10).map {
                    LineChartData.Point(Random.nextDouble(5.0, 20.0).toFloat(), "#$it")
                }
            )
        )
    }
}
