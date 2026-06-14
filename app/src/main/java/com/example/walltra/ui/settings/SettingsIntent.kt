package com.example.walltra.ui.settings

import com.example.walltra.data.model.ThemeMode

sealed class SettingsIntent {
    data class SelectThemeMode(val mode: ThemeMode) : SettingsIntent()
    data class SelectCurrency(val currency: String) : SettingsIntent()
}