package com.example.walltra.ui.periods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walltra.data.model.Period
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
class PeriodsViewModel @Inject constructor(
    private val periodRepository: PeriodRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PeriodsState())
    val state: StateFlow<PeriodsState> = _state.asStateFlow()

    init {
        observeData()
    }

    fun onIntent(intent: PeriodsIntent) {
        when (intent) {
            is PeriodsIntent.DeletePeriod -> deletePeriod(intent.period)
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            try {
                periodRepository.getAllPeriods().collect { periods ->
                    val withTotals = periods.map { period ->
                        val expenses = expenseRepository
                            .getExpensesBetweenDates(period.startDate, period.endDate)
                            .first()
                        PeriodWithTotal(period = period, total = expenses.sumOf { it.amount })
                    }
                    _state.update { it.copy(periods = withTotals, isLoading = false, error = null) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun deletePeriod(period: Period) {
        viewModelScope.launch {
            try {
                periodRepository.delete(period)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}