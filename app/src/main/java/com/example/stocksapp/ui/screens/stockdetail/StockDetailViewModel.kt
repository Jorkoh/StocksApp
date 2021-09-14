package com.example.stocksapp.ui.screens.stockdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stocksapp.data.datastore.SettingsDataStore
import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.repositories.stocks.ChartRange
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
    private val settings: SettingsDataStore,
    @Assisted private val symbol: String
) : ViewModel() {

    private val _stockDetailUIState = MutableStateFlow(StockDetailUIState(symbol))
    val stockDetailUIState: StateFlow<StockDetailUIState> = _stockDetailUIState

    private var getChartJob: Job? = null
    private var getCompanyInfoJob: Job? = null

    init {
        getIsTracked()
        getChartRange()
        getCompanyInfo()
    }

    private fun getIsTracked() {
        viewModelScope.launch {
            stocksRepository.fetchIsTracked(symbol).collect { isTracked ->
                _stockDetailUIState.value = _stockDetailUIState.value.copy(isTracked = isTracked)
            }
        }
    }

    private fun getChartRange() {
        viewModelScope.launch {
            settings.chartRange.collect { chartRange ->
                _stockDetailUIState.value = _stockDetailUIState.value.copy(chartRange = chartRange)
                getChart(chartRange)
            }
        }
    }

    private fun getChart(chartRange: ChartRange) {
        getChartJob?.cancel()
        getChartJob = viewModelScope.launch {
            stocksRepository.fetchChartPrices(
                symbol = symbol,
                range = chartRange,
                onStart = {
                    _stockDetailUIState.value = _stockDetailUIState.value.copy(
                        chartUIState = when (val previousChartState = _stockDetailUIState.value.chartUIState) {
                            is StockDetailUIState.ChartUIState.Error -> StockDetailUIState.ChartUIState.Working()
                            is StockDetailUIState.ChartUIState.Working -> StockDetailUIState.ChartUIState.Working(
                                chartData = previousChartState.chartData
                            )
                        }
                    )
                },
                onError = { message ->
                    _stockDetailUIState.value = _stockDetailUIState.value.copy(
                        chartUIState = StockDetailUIState.ChartUIState.Error(message)
                    )
                }
            ).collect { chartPrices ->
                _stockDetailUIState.value = _stockDetailUIState.value.copy(
                    chartUIState = StockDetailUIState.ChartUIState.Working(
                        chartData = LineChartData(chartPrices.map {
                            LineChartData.Point(it.closePrice.toFloat(), it.date.toString())
                        }),
                        loading = false
                    )
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

    fun changeChartRange(newRange: ChartRange) {
        viewModelScope.launch {
            settings.setChartRange(newRange)
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

data class StockDetailUIState(
    val symbol: String,
    val isTracked: Boolean = false,
    val chartRange: ChartRange? = null,
    val companyInfoUIState: CompanyInfoUIState = CompanyInfoUIState.Loading,
    val chartUIState: ChartUIState = ChartUIState.Working(),
) {
    sealed class CompanyInfoUIState {
        object Loading : CompanyInfoUIState()
        class Success(val companyInfo: CompanyInfo) : CompanyInfoUIState()
        class Error(val message: String) : CompanyInfoUIState()
    }

    sealed class ChartUIState {
        class Working(val chartData: LineChartData = LineChartData(), val loading: Boolean = true) : ChartUIState()
        class Error(val message: String) : ChartUIState()
    }
}

@AssistedFactory
interface StockDetailViewModelFactory {
    fun create(symbol: String): StockDetailViewModel
}
