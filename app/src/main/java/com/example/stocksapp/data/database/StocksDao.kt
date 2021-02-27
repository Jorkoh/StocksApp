package com.example.stocksapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.model.News
import com.example.stocksapp.data.model.Quote
import kotlinx.coroutines.flow.Flow

@Dao
interface StocksDao {

    @Transaction
    suspend fun refreshTopActiveQuotes(topActiveQuotes: List<Quote>) {
        removeTopActiveFromQuotes()
        insertQuotes(topActiveQuotes)
    }

    @Query("UPDATE quotes SET isTopActive=0")
    fun removeTopActiveFromQuotes()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<Quote>)

    @Query("SELECT * FROM quotes WHERE isTopActive = :isTopActive AND timestamp >= :timestampCutoff")
    fun getQuotesByActivity(isTopActive: Boolean, timestampCutoff: Long = 0L): Flow<List<Quote>>

    @Transaction
    suspend fun refreshNews(updatedNews: List<News>) {
        deleteNews()
        insertNews(updatedNews)
    }

    @Query("DELETE FROM news")
    fun deleteNews()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<News>)

    @Query("SELECT * FROM news WHERE timestamp >= :timestampCutoff")
    fun getNews(timestampCutoff: Long = 0L): Flow<List<News>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyInfo(companyInfo: CompanyInfo)

    @Query("SELECT * FROM company_infos WHERE symbol = :symbol AND timestamp >= :timestampCutoff")
    fun getCompanyInfo(symbol: String, timestampCutoff: Long = 0L): Flow<CompanyInfo?>
}
