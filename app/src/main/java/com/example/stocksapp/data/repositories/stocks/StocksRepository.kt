package com.example.stocksapp.data.repositories.stocks

import androidx.annotation.WorkerThread
import com.example.stocksapp.data.database.StocksDao
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class StocksRepository @Inject constructor(
    private val IEXService: IEXService,
    private val stocksDao: StocksDao
) {

    @WorkerThread
    fun fetchCompanyInfo(
        symbol: String,
        onStart: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        // TODO time the data when inserted on Room and get it only if recent as a cache (1 day?)
        val companyInfo = stocksDao.getCompanyInfo(symbol)
        if (companyInfo == null) {
            val response = IEXService.fetchCompanyInfo(symbol)
            response.suspendOnSuccess {
                data?.let { response ->
                    stocksDao.insertCompanyInfo(response)
                    emit(response)
                }
            }.onError {
                onError("Request failed with code ${statusCode.code}: $raw")
            }.onException {
                onError("Error while requesting: $message")
            }
        } else {
            emit(companyInfo)
        }
    }.onStart { onStart() }.flowOn(Dispatchers.IO)
}