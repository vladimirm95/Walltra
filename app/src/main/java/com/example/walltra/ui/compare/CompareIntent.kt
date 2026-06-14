package com.example.walltra.ui.compare

import com.example.walltra.data.model.Period

sealed class CompareIntent {
    data class SelectPeriodA(val period: Period) : CompareIntent()
    data class SelectPeriodB(val period: Period) : CompareIntent()
}