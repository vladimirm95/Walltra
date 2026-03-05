package com.example.walltra.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walltra.data.model.DefaultCategories
import com.example.walltra.data.model.Period
import com.example.walltra.data.repository.CategoryRepository
import com.example.walltra.data.repository.ExpenseRepository
import com.example.walltra.data.repository.PeriodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val periodRepository: PeriodRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        initializeCategories()
        startNewPeriod()
        observeData()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectDate -> selectDate(intent.date)
            is HomeIntent.NavigateMonth -> navigateMonth(intent.forward)
            is HomeIntent.StartNewPeriod -> startNewPeriod()
            is HomeIntent.SavePeriod -> _state.update { it.copy(isLoading = false) }
            is HomeIntent.ConfirmSavePeriod -> confirmSavePeriod()
            is HomeIntent.DismissSavePeriod -> _state.update { it.copy(isLoading = false) }
        }
    }

    private fun initializeCategories() {
        viewModelScope.launch {
            try {
                categoryRepository.insertAll(DefaultCategories.all)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun startNewPeriod() {
        _state.update {
            it.copy(
                currentPeriodStartDate = LocalDate.now(),
                selectedDate = LocalDate.now(),
                currentMonth = LocalDate.now().withDayOfMonth(1)
            )
        }
    }

    private fun selectDate(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
    }

    private fun navigateMonth(forward: Boolean) {
        _state.update {
            val newMonth = if (forward) {
                it.currentMonth.plusMonths(1)
            } else {
                it.currentMonth.minusMonths(1)
            }
            it.copy(currentMonth = newMonth)
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            try {
                combine(
                    categoryRepository.getAllCategories(),
                    expenseRepository.getExpensesBetweenDates(
                        startDate = _state.value.currentPeriodStartDate?.format(formatter) ?: LocalDate.now().format(formatter),
                        endDate = LocalDate.now().format(formatter)
                    )
                ) { categories, expenses ->
                    val totalByDate = expenses
                        .groupBy { it.date }
                        .mapValues { entry -> entry.value.sumOf { it.amount } }

                    _state.value.copy(
                        categories = categories,
                        expenses = expenses,
                        totalAmountByDate = totalByDate,
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

    private fun confirmSavePeriod() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val startDate = _state.value.currentPeriodStartDate ?: return@launch
                val period = Period(
                    id = UUID.randomUUID().toString(),
                    startDate = startDate.format(formatter),
                    endDate = LocalDate.now().format(formatter)
                )
                periodRepository.insert(period)
                startNewPeriod()
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}