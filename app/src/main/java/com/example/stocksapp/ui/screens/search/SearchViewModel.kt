package com.example.stocksapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocksapp.data.model.Symbol
import com.example.stocksapp.data.repositories.stocks.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {

    private val _searchUIState = MutableStateFlow<SearchUIState>(SearchUIState.Loading)
    val searchUIState: StateFlow<SearchUIState> = _searchUIState

    private var getQueriedSymbolsJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        getQueriedSymbolsJob?.cancel()
        getQueriedSymbolsJob = viewModelScope.launch {
            stocksRepository.fetchSymbols(
                query = newQuery,
                onStart = { _searchUIState.value = SearchUIState.Loading },
                onError = { _searchUIState.value = SearchUIState.Error(it) }
            ).collect { results ->
                _searchUIState.value = SearchUIState.Success(results)
            }
        }
    }
}

sealed class SearchUIState {
    object Loading : SearchUIState()
    data class Success(val results: List<Symbol>) : SearchUIState()
    data class Error(val message: String) : SearchUIState()
}