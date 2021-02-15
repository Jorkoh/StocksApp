package com.example.stocksapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stocksapp.data.model.CompanyInfo

@Dao
interface StocksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyInfo(companyInfo: CompanyInfo)

    @Query("SELECT * FROM company_infos WHERE symbol = :symbol")
    suspend fun getCompanyInfo(symbol: String): CompanyInfo?
}
