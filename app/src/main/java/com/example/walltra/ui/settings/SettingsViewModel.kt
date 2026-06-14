package com.example.walltra.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walltra.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val state: StateFlow<SettingsState> = combine(
        settingsRepository.themeMode,
        settingsRepository.currency
    ) { themeMode, currency ->
        SettingsState(themeMode = themeMode, currency = currency)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

    fun onIntent(intent: SettingsIntent) {
        viewModelScope.launch {
            when (intent) {
                is SettingsIntent.SelectThemeMode -> settingsRepository.setThemeMode(intent.mode)
                is SettingsIntent.SelectCurrency -> settingsRepository.setCurrency(intent.currency)
            }
        }
    }
}