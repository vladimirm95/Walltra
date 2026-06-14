package com.example.walltra.ui.day

import com.example.walltra.data.model.Category
import com.example.walltra.data.model.Expense
import java.time.LocalDate

data class DayState(
    val date: LocalDate = LocalDate.now(),
    val expenses: List<Expense> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)