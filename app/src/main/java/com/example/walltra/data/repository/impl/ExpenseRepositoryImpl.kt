package com.example.walltra.data.repository.impl

import com.example.walltra.data.local.dao.ExpenseDao
import com.example.walltra.data.model.Expense
import com.example.walltra.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override fun getExpensesByDate(date: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByDate(date)
    }

    override fun getExpensesBetweenDates(startDate: String, endDate: String): Flow<List<Expense>> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate)
    }

    override fun getTotalAmountByDate(date: String): Flow<Double?> {
        return expenseDao.getTotalAmountByDate(date)
    }

    override fun getExpensesByCategoryAndDateRange(
        categoryId: String,
        startDate: String,
        endDate: String
    ): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategoryAndDateRange(categoryId, startDate, endDate)
    }

    override suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    override suspend fun update(expense: Expense) {
        expenseDao.update(expense)
    }

    override suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }
}