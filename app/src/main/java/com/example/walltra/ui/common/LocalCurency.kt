package com.example.walltra.ui.common

import androidx.compose.runtime.compositionLocalOf

val LocalCurrency = compositionLocalOf { "RSD" }

fun Double.formatAmount(currency: String): String {
    return "${"%.0f".format(this)} $currency"
}