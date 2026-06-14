package com.example.walltra.ui.settings

import com.example.walltra.data.model.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val currency: String = "RSD"
)