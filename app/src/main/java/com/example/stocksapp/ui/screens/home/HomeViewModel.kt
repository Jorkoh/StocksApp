package com.example.stocksapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.data.repositories.stocks.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {
    private val _activeSymbolsUIState =
        MutableStateFlow<ActiveSymbolsUIState>(ActiveSymbolsUIState.Loading)
    val activeSymbolsUIState: StateFlow<ActiveSymbolsUIState> = _activeSymbolsUIState

    init {
        viewModelScope.launch {
            getTopActiveQuotes()
        }
    }

    private suspend fun getTopActiveQuotes() {
        stocksRepository.fetchTopActiveQuotes(
            onStart = { _activeSymbolsUIState.value = ActiveSymbolsUIState.Loading },
            onError = { _activeSymbolsUIState.value = ActiveSymbolsUIState.Error(it) }
        ).collect { quotes ->
            _activeSymbolsUIState.value = ActiveSymbolsUIState.Success(quotes)
        }
    }
}

sealed class ActiveSymbolsUIState {
    object Loading : ActiveSymbolsUIState()
    data class Success(val quotes: List<Quote>) : ActiveSymbolsUIState()
    data class Error(val message: String) : ActiveSymbolsUIState()
}
