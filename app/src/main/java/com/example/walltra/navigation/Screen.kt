package com.example.walltra.navigation

import kotlinx.serialization.Serializable

sealed class Screen {

    @Serializable
    data object Home : Screen()

    @Serializable
    data class Day(val date: String) : Screen()

    @Serializable
    data object Periods : Screen()

    @Serializable
    data class PeriodDetail(val periodId: String) : Screen()

    @Serializable
    data object Compare : Screen()

    @Serializable
    data object Settings : Screen()
}