package com.example.stocksapp.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.stocksapp.data.repositories.stocks.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {

}