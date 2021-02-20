package com.example.stocksapp.data.repositories.stocks

import androidx.annotation.WorkerThread
import com.example.stocksapp.data.database.StocksDao
import com.example.stocksapp.data.model.MostActiveSymbols
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.data.model.network.QuoteResponse
import com.example.stocksapp.data.model.utils.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class StocksRepository @Inject constructor(
    private val IEXService: IEXService,
    private val stocksDao: StocksDao
) {
    @WorkerThread
    fun fetchMostActiveSymbols(
        count: Int = 20,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        Timber.d("Fetching most active symbols")
        val cachedMostActiveSymbols = stocksDao.getMostActiveSymbols(1.hoursToTimestampCutoff())
        if (cachedMostActiveSymbols == null) {
            Timber.d("No cached most active symbols, fetching them from service")
            IEXService.fetchMostActiveSymbols(count).suspendOnSuccess {
                data?.let { quotesResponse ->
                    val newMostActiveSymbols = MostActiveSymbols(quotesResponse.map(QuoteResponse::symbol))
                    stocksDao.refreshMostActiveSymbols(newMostActiveSymbols)
                    val quotes = quotesResponse.map(QuoteResponse::map)
                    stocksDao.insertQuotes(quotes)
                    emit(quotes)
                }
            }.onError {
                onError("Request failed with code ${statusCode.code}: $raw")
            }.onException {
                onError("Error while requesting: $message")
            }
        } else {
            Timber.d("Cached most active symbols, fetching quotes")
            emit(fetchQuotes(cachedMostActiveSymbols.symbols, {}, onError).toList())
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun fetchQuote(
        symbol: String,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        Timber.d("Fetching quote for symbol $symbol")
        val cachedQuote = stocksDao.getQuote(symbol, 1.hoursToTimestampCutoff())
        if (cachedQuote == null) {
            Timber.d("No cached quote for symbol $symbol, fetching it from service")
            IEXService.fetchQuote(symbol).suspendOnSuccess {
                data?.let { quoteResponse ->
                    val quote = quoteResponse.map()
                    stocksDao.insertQuotes(listOf(quote))
                    emit(quote)
                }
            }.onError {
                onError("Request failed with code ${statusCode.code}: $raw")
            }.onException {
                onError("Error while requesting: $message")
            }
        } else {
            Timber.d("Cached quote for symbol $symbol")
            emit(cachedQuote)
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun fetchQuotes(
        symbols: List<String>,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        Timber.d("Fetching quotes for ${symbols.size} symbols")
        val cachedQuotes = stocksDao.getQuotes(symbols, 1.hoursToTimestampCutoff())
        Timber.d("Cached quotes for ${cachedQuotes.size} symbols")
        cachedQuotes.forEach { emit(it) }
        val nonCachedSymbols = symbols.minus(cachedQuotes.map(Quote::symbol))
        if (nonCachedSymbols.isNotEmpty()) {
            Timber.d("No cached quotes for ${nonCachedSymbols.size} symbols, fetching them from service")
            val symbolsQueryString = nonCachedSymbols.joinToString(",")
            IEXService.fetchQuotes(symbolsQueryString).suspendOnSuccess {
                data?.let { quoteResponse ->
                    val quotes = quoteResponse.map(QuoteResponse::map)
                    stocksDao.insertQuotes(quotes)
                    quotes.forEach { emit(it) }
                }
            }.onError {
                onError("Request failed with code ${statusCode.code}: $raw")
            }.onException {
                onError("Error while requesting: $message")
            }
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)


    @WorkerThread
    fun fetchCompanyInfo(
        symbol: String,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        Timber.d("Fetching company info")
        val cachedCompanyInfo = stocksDao.getCompanyInfo(symbol, 7.daysToTimestampCutoff())
        if (cachedCompanyInfo == null) {
            Timber.d("No cached company info, fetching it from service")
            IEXService.fetchCompanyInfo(symbol).suspendOnSuccess {
                data?.let { companyInfoResponse ->
                    val companyInfo = companyInfoResponse.map()
                    stocksDao.insertCompanyInfo(companyInfo)
                    emit(companyInfo)
                }
            }.onError {
                onError("Request failed with code ${statusCode.code}: $raw")
            }.onException {
                onError("Error while requesting: $message")
            }
        } else {
            Timber.d("Cached company info")
            emit(cachedCompanyInfo)
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)
}

// TODO: this isn't elegant at all
private fun Int.daysToTimestampCutoff() = (this * 24).hoursToTimestampCutoff()
private fun Int.hoursToTimestampCutoff() = (this * 60).minutesToTimestampCutoff()
private fun Int.minutesToTimestampCutoff() = Date().time - (this * 60000)