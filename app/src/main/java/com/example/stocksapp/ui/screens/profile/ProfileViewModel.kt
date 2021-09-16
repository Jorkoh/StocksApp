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

    fun SetIsDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setIsDarkMode(isDarkMode)
        }
    }
}