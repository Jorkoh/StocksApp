package com.example.stocksapp.ui.screens.stockdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stocksapp.data.repositories.stocks.StocksRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class StockDetailViewModel @AssistedInject constructor(
    private val stocksRepository: StocksRepository,
    @Assisted private val symbol: String
) : ViewModel() {

    companion object {
        fun provideFactory(
            assistedFactory: StockDetailViewModelFactory,
            symbol: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(symbol) as T
            }
        }
    }
}

@AssistedFactory
interface StockDetailViewModelFactory {
    fun create(symbol: String): StockDetailViewModel
}