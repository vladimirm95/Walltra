package com.example.walltra.ui.day

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.walltra.data.model.Expense
import com.example.walltra.data.repository.CategoryRepository
import com.example.walltra.data.repository.ExpenseRepository
import com.example.walltra.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dateString: String = savedStateHandle.toRoute<Screen.Day>().date

    private val _state = MutableStateFlow(DayState(date = LocalDate.parse(dateString)))
    val state: StateFlow<DayState> = _state.asStateFlow()

    init {
        observeData()
    }

    fun onIntent(intent: DayIntent) {
        when (intent) {
            is DayIntent.AddExpense -> addExpense(intent.name, intent.amount, intent.categoryId)
            is DayIntent.UpdateExpense -> updateExpense(intent.expense, intent.name, intent.amount, intent.categoryId)
            is DayIntent.DeleteExpense -> deleteExpense(intent.expense)
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            try {
                combine(
                    categoryRepository.getAllCategories(),
                    expenseRepository.getExpensesByDate(dateString)
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

    private fun addExpense(name: String, amount: Double, categoryId: String) {
        viewModelScope.launch {
            try {
                val normalizedName = name.trim()

                val existing = _state.value.expenses.find {
                    it.name.trim().equals(normalizedName, ignoreCase = true) &&
                            it.categoryId == categoryId
                }

                if (existing != null) {
                    expenseRepository.update(existing.copy(amount = existing.amount + amount))
                } else {
                    val expense = Expense(
                        id = UUID.randomUUID().toString(),
                        categoryId = categoryId,
                        name = normalizedName,
                        amount = amount,
                        date = dateString
                    )
                    expenseRepository.insert(expense)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun updateExpense(expense: Expense, name: String, amount: Double, categoryId: String) {
        viewModelScope.launch {
            try {
                expenseRepository.update(
                    expense.copy(name = name, amount = amount, categoryId = categoryId)
                )
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                expenseRepository.delete(expense)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}