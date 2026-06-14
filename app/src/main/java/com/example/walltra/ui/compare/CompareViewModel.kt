package com.example.walltra.ui.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walltra.data.model.Period
import com.example.walltra.data.repository.CategoryRepository
import com.example.walltra.data.repository.ExpenseRepository
import com.example.walltra.data.repository.PeriodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val periodRepository: PeriodRepository,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CompareState())
    val state: StateFlow<CompareState> = _state.asStateFlow()

    init {
        observePeriods()
        observeCategories()
    }

    fun onIntent(intent: CompareIntent) {
        when (intent) {
            is CompareIntent.SelectPeriodA -> selectPeriodA(intent.period)
            is CompareIntent.SelectPeriodB -> selectPeriodB(intent.period)
        }
    }

    private fun observePeriods() {
        viewModelScope.launch {
            periodRepository.getAllPeriods().collect { periods ->
                _state.update { current ->
                    val periodA = current.periodA?.takeIf { p -> periods.any { it.id == p.id } }
                        ?: periods.getOrNull(0)
                    val periodB = current.periodB?.takeIf { p -> periods.any { it.id == p.id } }
                        ?: periods.getOrNull(1)
                    current.copy(periods = periods, periodA = periodA, periodB = periodB, isLoading = false)
                }
                _state.value.periodA?.let { loadExpensesA(it) }
                _state.value.periodB?.let { loadExpensesB(it) }
            }
        }
    }

    private fun observeCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
    }

    private fun selectPeriodA(period: Period) {
        _state.update { it.copy(periodA = period) }
        loadExpensesA(period)
    }

    private fun selectPeriodB(period: Period) {
        _state.update { it.copy(periodB = period) }
        loadExpensesB(period)
    }

    private fun loadExpensesA(period: Period) {
        viewModelScope.launch {
            try {
                val expenses = expenseRepository
                    .getExpensesBetweenDates(period.startDate, period.endDate)
                    .first()
                _state.update { it.copy(expensesA = expenses, error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun loadExpensesB(period: Period) {
        viewModelScope.launch {
            try {
                val expenses = expenseRepository
                    .getExpensesBetweenDates(period.startDate, period.endDate)
                    .first()
                _state.update { it.copy(expensesB = expenses, error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}