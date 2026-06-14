package com.example.walltra.ui.periods

import com.example.walltra.data.model.Category
import com.example.walltra.data.model.Expense
import com.example.walltra.data.model.Period

data class PeriodDetailState(
    val period: Period? = null,
    val expenses: List<Expense> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)