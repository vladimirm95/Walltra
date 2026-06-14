package com.example.walltra.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walltra.data.model.DefaultCategories
import com.example.walltra.data.model.Expense
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

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
            is HomeIntent.SetMonth -> setMonth(intent.month)
            is HomeIntent.StartNewPeriod -> startNewPeriod()
            is HomeIntent.SavePeriod -> _state.update { it.copy(isLoading = false) }
            is HomeIntent.ConfirmSavePeriod -> confirmSavePeriod()
            is HomeIntent.DismissSavePeriod -> _state.update { it.copy(isLoading = false) }
            is HomeIntent.AddExpense -> addExpense(intent.name, intent.amount, intent.categoryId)
            is HomeIntent.SeedMockData -> seedMockData()
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

    private fun setMonth(month: LocalDate) {
        _state.update { it.copy(currentMonth = month) }
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

    private fun addExpense(name: String, amount: Double, categoryId: String) {
        viewModelScope.launch {
            try {
                val normalizedName = name.trim()
                val dateString = _state.value.selectedDate.format(formatter)

                val existing = _state.value.expenses.find {
                    it.name.trim().equals(normalizedName, ignoreCase = true) &&
                            it.categoryId == categoryId &&
                            it.date == dateString
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

    // TODO: privremeno - ukloni nakon testiranja
    private fun seedMockData() {
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val random = Random(System.currentTimeMillis())
                val categories = categoryRepository.getAllCategories().first()

                if (categories.isEmpty()) return@launch

                for (daysAgo in 0..40) {
                    val date = today.minusDays(daysAgo.toLong())
                    repeat(random.nextInt(0, 4)) {
                        val category = categories.random(random)
                        expenseRepository.insert(
                            Expense(
                                id = UUID.randomUUID().toString(),
                                categoryId = category.id,
                                name = "Test - ${category.name}",
                                amount = random.nextInt(100, 3000).toDouble(),
                                date = date.format(formatter)
                            )
                        )
                    }
                }

                periodRepository.insert(
                    Period(
                        id = UUID.randomUUID().toString(),
                        startDate = today.minusDays(40).format(formatter),
                        endDate = today.minusDays(21).format(formatter)
                    )
                )
                periodRepository.insert(
                    Period(
                        id = UUID.randomUUID().toString(),
                        startDate = today.minusDays(20).format(formatter),
                        endDate = today.minusDays(1).format(formatter)
                    )
                )
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}