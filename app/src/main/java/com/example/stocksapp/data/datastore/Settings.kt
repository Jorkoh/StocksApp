package com.example.stocksapp.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.stocksapp.Settings
import com.example.stocksapp.data.repositories.stocks.ChartRange
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings = Settings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return Settings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) = t.writeTo(output)
}

private val Context.settingsDataStore by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)

class SettingsDataStore(appContext: Context) {

    private val dataStore = appContext.settingsDataStore

    val chartRange: Flow<ChartRange> = dataStore.data.map {
        if (it.hasChartRange()) it.chartRange.toApp() else chartRangeDefault
    }
    val isDarkMode: Flow<Boolean> = dataStore.data.map {
        if (it.hasIsDarkMode()) it.isDarkMode else isDarkModeDefault
    }
    val showRelevantNews: Flow<Boolean> = dataStore.data.map {
        if (it.hasShowRelevantNews()) it.showRelevantNews else showRelevantNewsDefault
    }
    val onlySearchStocks: Flow<Boolean> = dataStore.data.map {
        if (it.hasOnlySearchStocks()) it.onlySearchStocks else onlySearchStocksDefault
    }

    suspend fun setChartRange(chartRange: ChartRange) {
        dataStore.updateData { settings ->
            settings.toBuilder().setChartRange(chartRange.toProto()).build()
        }
    }

    suspend fun setIsDarkMode(isDarkMode: Boolean) {
        dataStore.updateData { settings ->
            settings.toBuilder().setIsDarkMode(isDarkMode).build()
        }
    }

    suspend fun setShowRelevantNews(showRelevantNews: Boolean) {
        dataStore.updateData { settings ->
            settings.toBuilder().setShowRelevantNews(showRelevantNews).build()
        }
    }

    suspend fun setOnlySearchStocks(onlySearchStocks: Boolean) {
        dataStore.updateData { settings ->
            settings.toBuilder().setOnlySearchStocks(onlySearchStocks).build()
        }
    }

    companion object{
        val chartRangeDefault = ChartRange.OneWeek
        const val isDarkModeDefault = false
        const val showRelevantNewsDefault = true
        const val onlySearchStocksDefault = false
    }
}

private fun ChartRange.toProto() = when (this) {
    ChartRange.OneWeek -> Settings.ChartRangeProto.ONE_WEEK
    ChartRange.OneMonth -> Settings.ChartRangeProto.ONE_MONTH
    ChartRange.ThreeMonths -> Settings.ChartRangeProto.THREE_MONTHS
    ChartRange.OneYear -> Settings.ChartRangeProto.ONE_YEAR
}

private fun Settings.ChartRangeProto.toApp() = when (this) {
    Settings.ChartRangeProto.ONE_WEEK -> ChartRange.OneWeek
    Settings.ChartRangeProto.ONE_MONTH -> ChartRange.OneMonth
    Settings.ChartRangeProto.THREE_MONTHS -> ChartRange.ThreeMonths
    Settings.ChartRangeProto.ONE_YEAR -> ChartRange.OneYear

    Settings.ChartRangeProto.UNRECOGNIZED -> ChartRange.DefaultRange
}