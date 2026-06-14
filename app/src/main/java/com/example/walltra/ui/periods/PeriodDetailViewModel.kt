package com.example.walltra.ui.periods

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.walltra.data.repository.CategoryRepository
import com.example.walltra.data.repository.ExpenseRepository
import com.example.walltra.data.repository.PeriodRepository
import com.example.walltra.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeriodDetailViewModel @Inject constructor(
    private val periodRepository: PeriodRepository,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val periodId: String = savedStateHandle.toRoute<Screen.PeriodDetail>().periodId

    private val _state = MutableStateFlow(PeriodDetailState())
    val state: StateFlow<PeriodDetailState> = _state.asStateFlow()

    init {
        loadPeriod()
    }

    private fun loadPeriod() {
        viewModelScope.launch {
            try {
                val period = periodRepository.getPeriodById(periodId)
                if (period == null) {
                    _state.update { it.copy(isLoading = false, error = "Period nije pronađen") }
                    return@launch
                }
                _state.update { it.copy(period = period) }

                combine(
                    categoryRepository.getAllCategories(),
                    expenseRepository.getExpensesBetweenDates(period.startDate, period.endDate)
                ) { categories, expenses ->
                    _state.value.copy(
                        categories = categories,
                        expenses = expenses,
                        isLoading = false,
                        error = null
                    )
                }
                    .catch { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
                    .collect { newState -> _state.update { newState } }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}