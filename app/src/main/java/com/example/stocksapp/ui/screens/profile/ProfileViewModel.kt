package com.example.stocksapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocksapp.data.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val isDarkMode = settingsDataStore.isDarkMode
    val showRelevantNews = settingsDataStore.showRelevantNews
    val onlySearchStocks = settingsDataStore.onlySearchStocks

    fun setIsDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setIsDarkMode(isDarkMode)
        }
    }

    fun setShowRelevantNews(showRelevantNews: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowRelevantNews(showRelevantNews)
        }
    }

    fun setOnlySearchStocks(onlySearchStocks: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setOnlySearchStocks(onlySearchStocks)
        }
    }
}