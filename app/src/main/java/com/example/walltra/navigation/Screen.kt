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
    data object Compare : Screen()
}