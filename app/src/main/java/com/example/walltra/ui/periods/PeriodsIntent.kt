package com.example.walltra.ui.periods

import com.example.walltra.data.model.Period

sealed class PeriodsIntent {
    data class DeletePeriod(val period: Period) : PeriodsIntent()
}