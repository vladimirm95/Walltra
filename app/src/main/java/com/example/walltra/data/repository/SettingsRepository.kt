package com.example.walltra.data.repository

import com.example.walltra.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>
    val currency: Flow<String>

    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setCurrency(currency: String)
}