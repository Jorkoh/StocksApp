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

    private val _searchUIState = MutableStateFlow<SearchUIState>(SearchUIState.Empty)
    val searchUIState: StateFlow<SearchUIState> = _searchUIState

    private var getQueriedSymbolsJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        getQueriedSymbolsJob?.cancel()
        if (newQuery.isBlank()) {
            _searchUIState.value = SearchUIState.Empty
        } else {
            getQueriedSymbolsJob = viewModelScope.launch {
                stocksRepository.fetchSymbols(
                    query = newQuery,
                    limit = 15,
                    onStart = {
                        _searchUIState.value = SearchUIState.InUse(
                            results = when (val state = _searchUIState.value){
                                is SearchUIState.InUse -> state.results
                                else -> emptyList()
                            },
                            loading = true,
                            query = newQuery
                        )
                    },
                    onError = { _searchUIState.value = SearchUIState.Error(it, _searchUIState.value.query) }
                ).collect { results ->
                    _searchUIState.value = SearchUIState.InUse(
                        results = results,
                        loading = false,
                        query = _searchUIState.value.query
                    )
                }
            }
        }
    }
}

sealed class SearchUIState(val query: String) {
    object Empty : SearchUIState("")
    class InUse(val results: List<Symbol>, loading: Boolean, query: String) : SearchUIState(query)
    class Error(val message: String, query: String) : SearchUIState(query)
}