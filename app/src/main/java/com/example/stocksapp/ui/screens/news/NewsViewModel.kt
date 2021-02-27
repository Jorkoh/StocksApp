package com.example.stocksapp.ui.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocksapp.data.model.News
import com.example.stocksapp.data.repositories.stocks.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val stocksRepository: StocksRepository
) : ViewModel() {
    private val _newsUIState = MutableStateFlow<NewsUIState>(NewsUIState.Loading)
    val newsUIState: StateFlow<NewsUIState> = _newsUIState

    init {
        viewModelScope.launch {
            getNews()
        }
    }

    private suspend fun getNews() {
        stocksRepository.fetchNews(
            onStart = { _newsUIState.value = NewsUIState.Loading },
            onError = { _newsUIState.value = NewsUIState.Error(it) }
        ).collect { quotes ->
            _newsUIState.value = NewsUIState.Success(quotes)
        }
    }
}

sealed class NewsUIState {
    object Loading : NewsUIState()
    data class Success(val news: List<News>) : NewsUIState()
    data class Error(val message: String) : NewsUIState()
}
