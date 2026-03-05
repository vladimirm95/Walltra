package com.example.walltra.ui.home

import com.example.walltra.data.model.Category
import com.example.walltra.data.model.Expense
import java.time.LocalDate

data class HomeState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPeriodStartDate: LocalDate? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: LocalDate = LocalDate.now().withDayOfMonth(1),
    val expenses: List<Expense> = emptyList(),
    val categories: List<Category> = emptyList(),
    val totalAmountByDate: Map<String, Double> = emptyMap()
)