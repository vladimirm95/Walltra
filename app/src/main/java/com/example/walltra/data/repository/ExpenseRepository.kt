package com.example.walltra.data.repository

import com.example.walltra.data.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpensesByDate(date: String): Flow<List<Expense>>
    fun getExpensesBetweenDates(startDate: String, endDate: String): Flow<List<Expense>>
    fun getTotalAmountByDate(date: String): Flow<Double?>
    fun getExpensesByCategoryAndDateRange(
        categoryId: String,
        startDate: String,
        endDate: String
    ): Flow<List<Expense>>
    suspend fun insert(expense: Expense)
    suspend fun update(expense: Expense)
    suspend fun delete(expense: Expense)
}