package com.example.stocksapp.data.repositories.stocks

import androidx.annotation.WorkerThread
import com.example.stocksapp.data.database.StocksDao
import com.example.stocksapp.data.model.utils.mapToCompanyInfo
import com.example.stocksapp.data.model.utils.mapToQuote
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
    fun fetchTopActiveQuotes(
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        Timber.d("Fetching top active quotes")
        stocksDao.getQuotesByActivity(isTopActive = true, timestampCutoff = 1.hoursToTimestampCutoff())
            .distinctUntilChanged()
            .collect { quotes ->
                if (quotes.isEmpty()) {
                    Timber.d("No cached top active quotes, fetching them from service")
                    IEXService.fetchMostActiveSymbols().suspendOnSuccess {
                        data?.let { quotesResponse ->
                            val newQuotes = quotesResponse.map { it.mapToQuote(isTopActive = true) }
                            Timber.d("Storing new top active quotes in DB")
                            stocksDao.refreshTopActiveQuotes(newQuotes)
                        }
                    }.onError {
                        onError("Request failed with code ${statusCode.code}: $raw")
                    }.onException {
                        onError("Error while requesting: $message")
                    }
                } else {
                    Timber.d("Emitting top active quotes from DB")
                    emit(quotes)
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
                    val companyInfo = companyInfoResponse.mapToCompanyInfo()
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