package com.example.stocksapp.data.database

import androidx.room.*
import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.model.Quote
import kotlinx.coroutines.flow.Flow

@Dao
interface StocksDao {

    @Transaction
    suspend fun refreshTopActiveQuotes(topActiveQuotes: List<Quote>) {
        removeTopActiveMarks()
        insertQuotes(topActiveQuotes)
    }

    @Query("UPDATE quotes SET isTopActive=0")
    fun removeTopActiveMarks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<Quote>)

    @Query("SELECT * FROM quotes WHERE isTopActive = :isTopActive AND timestamp >= :timestampCutoff")
    fun getQuotesByActivity(isTopActive: Boolean, timestampCutoff: Long = 0L): Flow<List<Quote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyInfo(companyInfo: CompanyInfo)

    @Query("SELECT * FROM company_infos WHERE symbol = :symbol AND timestamp >= :timestampCutoff")
    fun getCompanyInfo(symbol: String, timestampCutoff: Long = 0L): Flow<CompanyInfo?>
}
