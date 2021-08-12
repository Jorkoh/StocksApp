package com.example.stocksapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocksapp.data.model.Price
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.data.repositories.stocks.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {

    private var getUserSymbolsJob: Job? = null
    private val _trackedSymbolsUIState = MutableStateFlow<TrackedSymbolsUIState>(TrackedSymbolsUIState.Loading)
    val trackedSymbolsUIState: StateFlow<TrackedSymbolsUIState> = _trackedSymbolsUIState

    private var getActiveSymbolsJob: Job? = null
    private val _activeSymbolsUIState = MutableStateFlow<ActiveSymbolsUIState>(ActiveSymbolsUIState.Loading)
    val activeSymbolsUIState: StateFlow<ActiveSymbolsUIState> = _activeSymbolsUIState

    init {
        getUserSymbols()
        getTopActiveQuotes()
    }

    private fun getUserSymbols() {
        getUserSymbolsJob?.cancel()
        getUserSymbolsJob = viewModelScope.launch {
            stocksRepository.fetchTrackedSymbols(
                onStart = { _trackedSymbolsUIState.value = TrackedSymbolsUIState.Loading },
                onError = { _trackedSymbolsUIState.value = TrackedSymbolsUIState.Error(it) }
            ).collect { chartPrices ->
                _trackedSymbolsUIState.value = TrackedSymbolsUIState.Success(chartPrices)
            }
        }
    }

    private fun getTopActiveQuotes() {
        getActiveSymbolsJob?.cancel()
        getActiveSymbolsJob = viewModelScope.launch {
            stocksRepository.fetchTopActiveQuotes(
                onStart = { _activeSymbolsUIState.value = ActiveSymbolsUIState.Loading },
                onError = { _activeSymbolsUIState.value = ActiveSymbolsUIState.Error(it) }
            ).collect { quotes ->
                _activeSymbolsUIState.value = ActiveSymbolsUIState.Success(quotes)
            }
        }
    }
}

sealed class TrackedSymbolsUIState {
    object Loading : TrackedSymbolsUIState()
    data class Success(val chartPrices: List<List<Price>>) : TrackedSymbolsUIState()
    data class Error(val message: String) : TrackedSymbolsUIState()
}

sealed class ActiveSymbolsUIState {
    object Loading : ActiveSymbolsUIState()
    data class Success(val quotes: List<Quote>) : ActiveSymbolsUIState()
    data class Error(val message: String) : ActiveSymbolsUIState()
}
