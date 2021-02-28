package com.example.stocksapp.ui.screens.stockdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.repositories.stocks.StocksRepository
import com.example.stocksapp.ui.components.charts.line.LineChartData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StockDetailViewModel @AssistedInject constructor(
    private val stocksRepository: StocksRepository,
    @Assisted private val symbol: String
) : ViewModel() {

    private val _chartUIState = MutableStateFlow<ChartUIState>(ChartUIState.Loading)
    val chartUIState: StateFlow<ChartUIState> = _chartUIState

    private val _companyInfoUIState =
        MutableStateFlow<CompanyInfoUIState>(CompanyInfoUIState.Loading(symbol))
    val companyInfoUIState: StateFlow<CompanyInfoUIState> = _companyInfoUIState

    init {
        viewModelScope.launch {
            getCompanyInfo(symbol)
        }
        viewModelScope.launch {
            getChart(symbol)
        }
    }

    private suspend fun getCompanyInfo(symbol: String) {
        stocksRepository.fetchCompanyInfo(
            symbol = symbol,
            onStart = {
                _companyInfoUIState.value = CompanyInfoUIState.Loading(symbol)
            },
            onError = { message ->
                _companyInfoUIState.value = CompanyInfoUIState.Error(symbol, message)
            }
        ).collect { companyInfo ->
            _companyInfoUIState.value = CompanyInfoUIState.Success(symbol, companyInfo)
        }
    }

    private suspend fun getChart(symbol: String) {
        stocksRepository.fetchChart(
            symbol = symbol,
            onStart = {
                _chartUIState.value = ChartUIState.Loading
            },
            onError = { message ->
                _chartUIState.value = ChartUIState.Error(message)
            }
        ).collect { chartData ->
            _chartUIState.value = ChartUIState.Success(chartData)
        }
    }

    // TODO: temp for testing
    fun refreshChartData() {
        viewModelScope.launch {
            getChart(symbol)
        }
    }

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

sealed class CompanyInfoUIState(val symbol: String) {
    class Loading(symbol: String) : CompanyInfoUIState(symbol)
    class Success(symbol: String, val companyInfo: CompanyInfo) : CompanyInfoUIState(symbol)
    class Error(symbol: String, val message: String) : CompanyInfoUIState(symbol)
}

sealed class ChartUIState {
    object Loading : ChartUIState()
    class Success(val chartData: LineChartData) : ChartUIState()
    class Error(val message: String) : ChartUIState()
}

@AssistedFactory
interface StockDetailViewModelFactory {
    fun create(symbol: String): StockDetailViewModel
}
