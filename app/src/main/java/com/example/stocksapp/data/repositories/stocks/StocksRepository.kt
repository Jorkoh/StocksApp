package com.example.stocksapp.data.repositories.stocks

import androidx.annotation.WorkerThread
import com.example.stocksapp.data.database.StocksDao
import com.example.stocksapp.data.model.Price
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.data.model.utils.SuccessCompanyInfoMapper
import com.example.stocksapp.data.model.utils.SuccessNewsMapper
import com.example.stocksapp.data.model.utils.SuccessQuotesMapper
import com.example.stocksapp.data.model.utils.SuccessSymbolsMapper
import com.example.stocksapp.data.model.utils.mapToPrice
import com.example.stocksapp.data.repositories.stocks.IEXService.ChartRanges.OneMonth
import com.example.stocksapp.data.repositories.stocks.IEXService.ChartRanges.OneWeek
import com.example.stocksapp.data.repositories.stocks.IEXService.ChartRanges.OneYear
import com.example.stocksapp.data.repositories.stocks.IEXService.ChartRanges.ThreeMonths
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class StocksRepository @Inject constructor(
    private val IEXService: IEXService,
    private val stocksDao: StocksDao
) {

    companion object {
        const val MAX_NEWS = 10
        const val MOST_ACTIVE_COUNT = 20
    }

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
    ) = flow<List<Pair<Quote, List<Price>>>> {
        stocksDao.getTrackedSymbols().collect { trackedSymbols ->
            if (trackedSymbols.isEmpty()) {
                emit(emptyList())
            } else {
                val quotesFlow = fetchQuotes(
                    symbols = trackedSymbols.map { it.symbol },
                    onStart = {},
                    onError = { onError(it) }
                )
                val chartPricesFlow = combine(trackedSymbols.map { trackedSymbol ->
                    fetchChartPrices(
                        symbol = trackedSymbol.symbol,
                        range = OneWeek,
                        onStart = {},
                        onError = { onError(it) }
                    )
                }) { it.toList() }

                combine(quotesFlow, chartPricesFlow) { quotes, chartPrices ->
                    trackedSymbols.map { symbol ->
                        Pair(
                            first = quotes.first { it.symbol == symbol.symbol },
                            second = chartPrices.first { it.first().symbol == symbol.symbol }
                        )
                    }
                }.first {
                    emit(it)
                    true
                }
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
        val firstDate = lastDate.run {
            when (range) {
                OneWeek -> minus(1, ChronoUnit.WEEKS)
                OneMonth -> minus(1, ChronoUnit.MONTHS)
                ThreeMonths -> minus(3, ChronoUnit.MONTHS)
                OneYear -> minus(1, ChronoUnit.YEARS)
            }.plusDays(1)
        }
        val daysBetween = firstDate.until(lastDate).plusDays(1).days

        stocksDao.getChartPrices(
            symbol = symbol,
            firstDate = firstDate,
            lastDate = lastDate
        ).first { cachedPrices ->
            if (cachedPrices.size != daysBetween) {
                // API fetch TODO: if the missing dates are all at the end only fetch that missing range
                IEXService.fetchChartPrices(symbol, range).suspendOnSuccess {
                    val timestamp = Instant.now()
                    val apiPrices = data.map { it.mapToPrice(symbol, timestamp) }
                    val missingPrices = generateMissingPrices(
                        symbol = symbol,
                        apiPrices = apiPrices,
                        firstDate = firstDate,
                        lastDate = lastDate,
                        timestamp = timestamp
                    )
                    stocksDao.insertChartPrices(apiPrices + missingPrices)
                }.onError {
                    onError("Request failed with code ${statusCode.code}: $raw")
                }.onException {
                    onError("Error while requesting: $message")
                }
                false
            } else {
                // DB cache
                emit(cachedPrices)
                true
            }
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)

    private fun generateMissingPrices(
        symbol: String,
        apiPrices: List<Price>,
        firstDate: LocalDate,
        lastDate: LocalDate,
        timestamp: Instant
    ): MutableList<Price> {
        val missingPrices = mutableListOf<Price>()
        if (!apiPrices.first().date.isEqual(firstDate)) {
            // missing days at the start, add them
            var runningDate = firstDate
            do {
                missingPrices.add(generateEmptyPrice(symbol, runningDate, apiPrices.first().closePrice, timestamp))
                runningDate = runningDate.plusDays(1)
            } while (!runningDate.isEqual(apiPrices.first().date))
        }
        apiPrices.zipWithNext { firstPrice, secondPrice ->
            if (!firstPrice.date.plusDays(1).isEqual(secondPrice.date)) {
                // days are not consecutive, need to fill the days between
                var runningDate = firstPrice.date.plusDays(1)
                do {
                    missingPrices.add(generateEmptyPrice(symbol, runningDate, firstPrice.closePrice, timestamp))
                    runningDate = runningDate.plusDays(1)
                } while (!runningDate.isEqual(secondPrice.date))
            }
        }
        if (!apiPrices.last().date.isEqual(lastDate)) {
            // missing days at the start, add them
            var runningDate = firstDate
            do {
                missingPrices.add(generateEmptyPrice(symbol, runningDate, apiPrices.last().closePrice, timestamp))
                runningDate = runningDate.plusDays(1)
            } while (!runningDate.isEqual(apiPrices.last().date))
        }
        return missingPrices
    }

    private fun generateEmptyPrice(
        symbol: String,
        date: LocalDate,
        closePrice: Double,
        timestamp: Instant
    ) = Price(
        symbol = symbol,
        date = date,
        closePrice = closePrice,
        volume = 0,
        change = 0.0,
        changePercent = 0.0,
        changeOverTime = 0.0,
        noDataDay = true,
        earliestAvailable = false,
        fetchTimestamp = timestamp
    )

    @WorkerThread
    fun fetchQuotes(
        symbols: List<String>,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        stocksDao.getQuotes(
            symbols = symbols,
            timestampCutoff = Instant.now().minus(1, ChronoUnit.HOURS)
        ).distinctUntilChanged().collect { cachedQuotes -> // TODO why is distinctUntilChanged needed?
            if (cachedQuotes.size != symbols.size) {
                val missingSymbols = symbols.minus(cachedQuotes.map { it.symbol })
                // API fetch
                IEXService.fetchQuotes(missingSymbols.joinToString(",")).suspendOnSuccess(SuccessQuotesMapper) {
                    stocksDao.insertQuotes(this)
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
    fun fetchTopActiveQuotes(
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        stocksDao.getQuotesByActivity(
            isTopActive = true,
            timestampCutoff = Instant.now().minus(1, ChronoUnit.HOURS)
        ).distinctUntilChanged().collect { cachedQuotes -> // TODO why is distinctUntilChanged needed?
            if (cachedQuotes.isEmpty()) { // TODO can there be cases where this check is not enough?
                // API fetch
                IEXService.fetchMostActiveSymbols(MOST_ACTIVE_COUNT).suspendOnSuccess(SuccessQuotesMapper) {
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
                    trackedSymbols.map { it.symbol }
                } else {
                    // If the user doesn't have tracked symbols fetch news from most active
                    // TODO take care of error fetching top active quotes
                    fetchTopActiveQuotes({}, {}).first().map { it.symbol }
                }
                // We want to load a max of ~10 news since its API is expensive
                IEXService.fetchNews(
                    symbols = newsSymbols.joinToString(separator = ","),
                    numberPerSymbol = (MAX_NEWS / newsSymbols.size).coerceAtLeast(1)
                ).suspendOnSuccess(SuccessNewsMapper) {
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
    fun fetchSymbols(
        query: String,
        limit: Int = Int.MAX_VALUE,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        val symbols = stocksDao.getSymbolsByQuery(query, limit)
        emit(symbols)
    }.onStart { onStart() }.flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun updateSymbols(
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) {
        onStart()
        IEXService.fetchSymbols().suspendOnSuccess(SuccessSymbolsMapper) {
            stocksDao.refreshSymbols(this)
        }.onError {
            onError("Request failed with code ${statusCode.code}: $raw")
        }.onException {
            onError("Error while requesting: $message")
        }
    }
}
