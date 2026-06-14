package com.example.walltra.ui.day

import com.example.walltra.data.model.Expense

sealed class DayIntent {
    data class AddExpense(val name: String, val amount: Double, val categoryId: String) : DayIntent()
    data class UpdateExpense(
        val expense: Expense,
        val name: String,
        val amount: Double,
        val categoryId: String
    ) : DayIntent()
    data class DeleteExpense(val expense: Expense) : DayIntent()
}