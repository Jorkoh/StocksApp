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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StockDetailViewModel @AssistedInject constructor(
    private val stocksRepository: StocksRepository,
    @Assisted private val symbol: String
) : ViewModel() {

    private val _stockDetailUIState = MutableStateFlow(StockDetailUIState(symbol))
    val stockDetailUIState: StateFlow<StockDetailUIState> = _stockDetailUIState

    private var getChartJob: Job? = null
    private var getCompanyInfoJob: Job? = null

    init {
        getIsTracked()
        getChart()
        getCompanyInfo()
    }

    private fun getIsTracked() {
        viewModelScope.launch {
            stocksRepository.fetchIsTracked(symbol).collect { isTracked ->
                _stockDetailUIState.value = _stockDetailUIState.value.copy(isTracked = isTracked)
            }
        }
    }

    private fun getChart() {
        getChartJob?.cancel()
        getChartJob = viewModelScope.launch {
            stocksRepository.fetchChartPrices(
                symbol = symbol,
                onStart = {
                    _stockDetailUIState.value = _stockDetailUIState.value.copy(
                        chartUIState = StockDetailUIState.ChartUIState.Loading
                    )
                },
                onError = { message ->
                    _stockDetailUIState.value = _stockDetailUIState.value.copy(
                        chartUIState = StockDetailUIState.ChartUIState.Error(message)
                    )
                }
            ).collect { chartData ->
                _stockDetailUIState.value = _stockDetailUIState.value.copy(
                    chartUIState = StockDetailUIState.ChartUIState.Success(chartData)
                )
            }
        }
    }

    private fun getCompanyInfo() {
        getCompanyInfoJob?.cancel()
        getCompanyInfoJob = viewModelScope.launch {
            stocksRepository.fetchCompanyInfo(
                symbol = symbol,
                onStart = {
                    _stockDetailUIState.value = _stockDetailUIState.value.copy(
                        companyInfoUIState = StockDetailUIState.CompanyInfoUIState.Loading
                    )
                },
                onError = { message ->
                    _stockDetailUIState.value = _stockDetailUIState.value.copy(
                        companyInfoUIState = StockDetailUIState.CompanyInfoUIState.Error(message)
                    )
                }
            ).collect { companyInfo ->
                _stockDetailUIState.value = _stockDetailUIState.value.copy(
                    companyInfoUIState = StockDetailUIState.CompanyInfoUIState.Success(companyInfo)
                )
            }
        }
    }

    fun toggleIsTracked() {
        viewModelScope.launch {
            stocksRepository.toggleIsTracked(symbol, !_stockDetailUIState.value.isTracked)
        }
    }

    // TODO: temp for testing
    fun refreshChartData() {
        getChart()
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

data class StockDetailUIState(
    val symbol: String,
    val isTracked: Boolean = false,
    val companyInfoUIState: CompanyInfoUIState = CompanyInfoUIState.Loading,
    val chartUIState: ChartUIState = ChartUIState.Loading,
) {
    sealed class CompanyInfoUIState {
        object Loading : CompanyInfoUIState()
        class Success(val companyInfo: CompanyInfo) : CompanyInfoUIState()
        class Error(val message: String) : CompanyInfoUIState()
    }

    sealed class ChartUIState {
        object Loading : ChartUIState()
        class Success(val chartData: LineChartData) : ChartUIState()
        class Error(val message: String) : ChartUIState()
    }
}

@AssistedFactory
interface StockDetailViewModelFactory {
    fun create(symbol: String): StockDetailViewModel
}
