package com.example.stocksapp.ui.screens.stockdetail

import androidx.lifecycle.ViewModel
import com.example.stocksapp.data.repositories.stocks.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {

}