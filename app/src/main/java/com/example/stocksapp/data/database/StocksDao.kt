package com.example.stocksapp.data.database

import androidx.room.*
import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.model.MostActiveSymbols
import com.example.stocksapp.data.model.Quote

@Dao
interface StocksDao {

    @Transaction
    suspend fun refreshMostActiveSymbols(mostActiveSymbols: MostActiveSymbols) {
        deleteMostActiveSymbols()
        insertMostActiveSymbols(mostActiveSymbols)
    }

    @Query("DELETE FROM most_active_symbols")
    suspend fun deleteMostActiveSymbols()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMostActiveSymbols(mostActiveSymbols: MostActiveSymbols)

    @Query("SELECT * FROM most_active_symbols WHERE timestamp >= :timestampCutoff ORDER BY timestamp DESC")
    suspend fun getMostActiveSymbols(timestampCutoff: Long = 0L): MostActiveSymbols?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<Quote>)

    @Query("SELECT * FROM quotes WHERE symbol = :symbol AND timestamp >= :timestampCutoff")
    suspend fun getQuote(symbol: String, timestampCutoff: Long = 0L) : Quote?

    @Query("SELECT * FROM quotes WHERE symbol IN (:symbols) AND timestamp >= :timestampCutoff")
    suspend fun getQuotes(symbols: List<String>, timestampCutoff: Long = 0L) : List<Quote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyInfo(companyInfo: CompanyInfo)

    // TODO figure out if stuff like this should return a Flow
    @Query("SELECT * FROM company_infos WHERE symbol = :symbol AND timestamp >= :timestampCutoff")
    suspend fun getCompanyInfo(symbol: String, timestampCutoff: Long = 0L): CompanyInfo?
}
