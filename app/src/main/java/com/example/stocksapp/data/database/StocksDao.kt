package com.example.stocksapp.data.database

import androidx.compose.ui.util.fastFirstOrNull
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.model.News
import com.example.stocksapp.data.model.Price
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.data.model.Symbol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate

@Dao
interface StocksDao {

    // SYMBOLS

    @Query("SELECT * FROM symbols WHERE symbol LIKE '%' ||:query ||'%'")
    suspend fun getSymbolsByQuery(query: String): List<Symbol>

    @Transaction
    suspend fun refreshSymbols(newSymbols: List<Symbol>) {
        val tracked = getTrackedSymbols().first()
        val newWithTracked = newSymbols.map { newSymbol ->
            tracked.fastFirstOrNull { it.symbol == newSymbol.symbol } ?: newSymbol
        }

        deleteSymbols()
        insertSymbols(newWithTracked)
    }

    @Query("DELETE FROM symbols")
    suspend fun deleteSymbols()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymbols(symbols: List<Symbol>)

    @Query("SELECT * FROM symbols WHERE userTracked = 1")
    fun getTrackedSymbols(): Flow<List<Symbol>>

    @Query("SELECT EXISTS(SELECT 1 FROM symbols WHERE userTracked = 1 AND symbol = :symbol)")
    fun symbolIsTracked(symbol: String): Flow<Boolean>

    @Query("UPDATE symbols SET userTracked = :isTracked WHERE symbol = :symbol")
    suspend fun updateIsTracked(symbol: String, isTracked: Boolean)

    // QUOTES

    @Transaction
    suspend fun refreshTopActiveQuotes(topActiveQuotes: List<Quote>) {
        removeTopActiveFromQuotes()
        insertQuotes(topActiveQuotes)
    }

    @Query("UPDATE quotes SET isTopActive = 0")
    fun removeTopActiveFromQuotes()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<Quote>)

    @Query("SELECT * FROM quotes WHERE symbol IN (:symbols) AND fetchTimestamp >= :timestampCutoff")
    fun getQuotes(symbols: List<String>, timestampCutoff: Instant): Flow<List<Quote>>

    @Query("SELECT * FROM quotes WHERE isTopActive = :isTopActive AND fetchTimestamp >= :timestampCutoff")
    fun getQuotesByActivity(isTopActive: Boolean, timestampCutoff: Instant): Flow<List<Quote>>

    // CHARTS

    @Query("SELECT * FROM prices WHERE symbol = :symbol AND date BETWEEN :firstDate AND :lastDate ORDER BY date")
    fun getChartPrices(symbol: String, firstDate: LocalDate, lastDate: LocalDate): Flow<List<Price>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChartPrices(prices: List<Price>)

    // NEWS

    @Transaction
    suspend fun refreshNews(updatedNews: List<News>) {
        deleteNews()
        insertNews(updatedNews)
    }

    @Query("DELETE FROM news")
    fun deleteNews()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<News>)

    @Query("SELECT * FROM news WHERE fetchTimestamp >= :timestampCutoff")
    fun getNews(timestampCutoff: Instant): Flow<List<News>>

    // COMPANY INFO

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyInfo(companyInfo: CompanyInfo)

    @Query("SELECT * FROM company_infos WHERE symbol = :symbol AND fetchTimestamp >= :timestampCutoff")
    fun getCompanyInfo(symbol: String, timestampCutoff: Instant): Flow<CompanyInfo?>
}
