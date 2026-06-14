package com.example.walltra.ui.periods

import com.example.walltra.data.model.Period

data class PeriodWithTotal(
    val period: Period,
    val total: Double
)

data class PeriodsState(
    val periods: List<PeriodWithTotal> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)