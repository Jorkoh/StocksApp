package com.example.stocksapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stocksapp.data.model.CompanyInfo
import com.example.stocksapp.data.model.News
import com.example.stocksapp.data.model.Price
import com.example.stocksapp.data.model.Quote
import com.example.stocksapp.data.model.TrackedSymbol

@Database(
    entities = [CompanyInfo::class, Price::class, Quote::class, News::class, TrackedSymbol::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stocksDao(): StocksDao

    companion object {
        private const val DATABASE_NAME = "stocksApp.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()
    }
}
