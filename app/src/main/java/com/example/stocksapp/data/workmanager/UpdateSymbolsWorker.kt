package com.example.stocksapp.data.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stocksapp.data.repositories.stocks.StocksRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class UpdateSymbolsWorker @AssistedInject constructor(
    private val stocksRepository: StocksRepository,
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        stocksRepository.updateSymbols(
            onStart = {},
            onError = { Result.failure() }
        )
        Result.success()
    }
}