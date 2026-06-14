package com.example.walltra.ui.compare

import com.example.walltra.data.model.Category
import com.example.walltra.data.model.Expense
import com.example.walltra.data.model.Period

data class CompareState(
    val periods: List<Period> = emptyList(),
    val periodA: Period? = null,
    val periodB: Period? = null,
    val categories: List<Category> = emptyList(),
    val expensesA: List<Expense> = emptyList(),
    val expensesB: List<Expense> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)