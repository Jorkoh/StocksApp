package com.example.stocksapp.di

import android.content.Context
import com.example.stocksapp.data.database.AppDatabase
import com.example.stocksapp.data.database.StocksDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideStocksDao(appDatabase: AppDatabase): StocksDao = appDatabase.stocksDao()
}