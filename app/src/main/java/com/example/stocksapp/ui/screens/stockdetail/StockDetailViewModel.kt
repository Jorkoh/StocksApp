package com.example.stocksapp.ui.screens.stockdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stocksapp.data.datastore.SettingsDataStore
import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.model.Quote
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class StockDetailViewModel @AssistedInject constructor(
    private val stocksRepository: StocksRepository,
    private val settings: SettingsDataStore,
    @Assisted private val symbol: String
) : ViewModel() {

    private val _stockDetailUIState = MutableStateFlow(StockDetailUIState(symbol))
    val stockDetailUIState: StateFlow<StockDetailUIState> = _stockDetailUIState

    private var getChartJob: Job? = null
    private var getNonChartInfoJob: Job? = null

    init {
        getIsTracked()
        getChart()
        getNonChartInfo()
    }

    private fun getIsTracked() {
        viewModelScope.launch {
            stocksRepository.fetchIsTracked(symbol).collect { isTracked ->
                _stockDetailUIState.value = _stockDetailUIState.value.copy(isTracked = isTracked)
            }
        }
    }

    private fun getChart() {
        viewModelScope.launch {
            settings.chartRange.collect { chartRange ->
                getChartJob?.cancel()
                getChartJob = viewModelScope.launch {
                    stocksRepository.fetchChartPrices(
                        symbol = symbol,
                        range = chartRange,
                        onStart = {
                            _stockDetailUIState.value = _stockDetailUIState.value.copy(
                                chartUIState = when (val previousChartState = _stockDetailUIState.value.chartUIState) {
                                    is StockDetailUIState.ChartUIState.Error -> StockDetailUIState.ChartUIState.Working(
                                        chartRange = chartRange
                                    )
                                    is StockDetailUIState.ChartUIState.Working -> StockDetailUIState.ChartUIState.Working(
                                        chartRange = chartRange,
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
                                chartRange = chartRange,
                                chartData = LineChartData(
                                    chartPrices.map {
                                        LineChartData.Point(it.closePrice.toFloat(), it.date.toString())
                                    },
                                    bottomPaddingRatio = 0.1f,
                                    topPaddingRatio = 0.1f
                                ),
                                loading = false
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getNonChartInfo() {
        getNonChartInfoJob?.cancel()
        getNonChartInfoJob = viewModelScope.launch {
            _stockDetailUIState.value = _stockDetailUIState.value.copy(
                nonChartUIState = StockDetailUIState.NonChartUIState.Loading
            )
            val companyInfoFlow = stocksRepository.fetchCompanyInfo(
                symbol = symbol,
                onStart = { },
                onError = { message ->
                    _stockDetailUIState.value = _stockDetailUIState.value.copy(
                        nonChartUIState = StockDetailUIState.NonChartUIState.Error(message)
                    )
                }
            )

            val quoteFlow = stocksRepository.fetchQuotes(
                symbols = listOf(symbol),
                onStart = { },
                onError = { message ->
                    _stockDetailUIState.value = _stockDetailUIState.value.copy(
                        nonChartUIState = StockDetailUIState.NonChartUIState.Error(message)
                    )
                }
            )

            combine(companyInfoFlow, quoteFlow) { companyInfo: CompanyInfo, quotes: List<Quote> ->
                Pair(companyInfo, quotes.first())
            }.collect { companyInfoAndQuote ->
                val (companyInfo, quote) = companyInfoAndQuote
                _stockDetailUIState.value = _stockDetailUIState.value.copy(
                    nonChartUIState = StockDetailUIState.NonChartUIState.Success(companyInfo, quote)
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

// TODO figure out a way to organize this state better
data class StockDetailUIState(
    val symbol: String,
    val isTracked: Boolean = false,
    val chartUIState: ChartUIState = ChartUIState.Working(),
    val nonChartUIState: NonChartUIState = NonChartUIState.Loading,
) {
    sealed class ChartUIState(val chartRange: ChartRange?) {
        class Working(
            val chartData: LineChartData = LineChartData(),
            val loading: Boolean = true,
            chartRange: ChartRange? = null
        ) : ChartUIState(chartRange)

        class Error(
            val message: String,
            chartRange: ChartRange? = null
        ) : ChartUIState(chartRange)
    }

    sealed class NonChartUIState {
        object Loading : NonChartUIState()
        class Success(val companyInfo: CompanyInfo, val quote: Quote) : NonChartUIState()
        class Error(val message: String) : NonChartUIState()
    }
}

@AssistedFactory
interface StockDetailViewModelFactory {
    fun create(symbol: String): StockDetailViewModel
}
