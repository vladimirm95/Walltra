package com.example.walltra.ui.home

import java.time.LocalDate

sealed class HomeIntent {
    data class SelectDate(val date: LocalDate) : HomeIntent()
    data class NavigateMonth(val forward: Boolean) : HomeIntent()
    data object StartNewPeriod : HomeIntent()
    data object SavePeriod : HomeIntent()
    data object ConfirmSavePeriod : HomeIntent()
    data object DismissSavePeriod : HomeIntent()
}