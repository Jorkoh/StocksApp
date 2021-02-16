package com.example.stocksapp.data.repositories.stocks

import androidx.annotation.WorkerThread
import com.example.stocksapp.data.database.StocksDao
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StocksRepository @Inject constructor(
    private val IEXService: IEXService,
    private val stocksDao: StocksDao
) {

    @WorkerThread
    fun fetchCompanyInfo(
        symbol: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = flow {
        // TODO time the data when inserted on Room and get it only if recent as a cache (1 day?)
        val companyInfo = stocksDao.getCompanyInfo(symbol)
        if (companyInfo == null) {
            val response = IEXService.fetchCompanyInfo(symbol)
            response.suspendOnSuccess {
                data?.let { response ->
                    stocksDao.insertCompanyInfo(response)
                    emit(response)
                    onSuccess()
                }
            }.onError {
                onError("[Error code ${statusCode.code}]: ${message()}")
            }.onException {
                onError(message)
            }
        } else {
            emit(companyInfo)
            onSuccess()
        }
    }.flowOn(Dispatchers.IO)
}