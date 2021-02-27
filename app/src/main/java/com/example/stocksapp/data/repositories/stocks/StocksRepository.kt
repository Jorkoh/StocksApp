package com.example.stocksapp.data.repositories.stocks

import androidx.annotation.WorkerThread
import com.example.stocksapp.data.database.StocksDao
import com.example.stocksapp.data.model.utils.mapToCompanyInfo
import com.example.stocksapp.data.model.utils.mapToNews
import com.example.stocksapp.data.model.utils.mapToQuote
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import java.util.Date
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
        stocksDao.getQuotesByActivity(
            isTopActive = true,
            timestampCutoff = 1.hoursToTimestampCutoff()
        )
            .distinctUntilChanged()
            .collect { quotes ->
                if (quotes.isEmpty()) {
                    Timber.d("No cached top active quotes, fetching them from service")
                    IEXService.fetchMostActiveSymbols().suspendOnSuccess {
                        data?.let { quotesResponse ->
                            val updatedQuotes = quotesResponse.map {
                                it.mapToQuote(Date().time, true)
                            }
                            Timber.d("Storing new top active quotes in DB")
                            stocksDao.refreshTopActiveQuotes(updatedQuotes)
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
    fun fetchNews(
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        Timber.d("Fetching news")
        stocksDao.getNews(timestampCutoff = 2.hoursToTimestampCutoff())
            .distinctUntilChanged()
            .collect { news ->
                if (news.isEmpty()) {
                    Timber.d("No cached news, fetching them from service")
                    // TODO: use symbols from watchlist or top quotes
                    IEXService.fetchNews("GME").suspendOnSuccess {
                        data?.let { newsResponse ->
                            val updatedNews = newsResponse.map {
                                it.mapToNews(Date().time)
                            }
                            Timber.d("Storing new news in DB")
                            stocksDao.refreshNews(updatedNews)
                        }
                    }.onError {
                        onError("Request failed with code ${statusCode.code}: $raw")
                    }.onException {
                        onError("Error while requesting: $message")
                    }
                } else {
                    Timber.d("Emitting news from DB")
                    emit(news)
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
        stocksDao.getCompanyInfo(symbol = symbol, timestampCutoff = 7.daysToTimestampCutoff())
            .distinctUntilChanged()
            .collect { companyInfo ->
                if (companyInfo == null) {
                    Timber.d("No cached company info, fetching them from service")
                    IEXService.fetchCompanyInfo(symbol).suspendOnSuccess {
                        data?.let { companyInfoResponse ->
                            val newCompanyInfo = companyInfoResponse.mapToCompanyInfo(Date().time)
                            Timber.d("Storing new company info in DB")
                            stocksDao.insertCompanyInfo(newCompanyInfo)
                        }
                    }.onError {
                        onError("Request failed with code ${statusCode.code}: $raw")
                    }.onException {
                        onError("Error while requesting: $message")
                    }
                } else {
                    Timber.d("Emitting company info from DB")
                    emit(companyInfo)
                }
            }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)
}

// TODO: this isn't elegant at all
private fun Int.daysToTimestampCutoff() = (this * 24).hoursToTimestampCutoff()
private fun Int.hoursToTimestampCutoff() = (this * 60).minutesToTimestampCutoff()
private fun Int.minutesToTimestampCutoff() = Date().time - (this * 60000)

enum class ChartRanges(private val urlString: String) {
    FiveDays("5d"),
    OneMonth("1m"),
    ThreeMonths("3m"),
    OneYear("1y"),
    All("max");

    override fun toString(): String {
        return urlString
    }
}
