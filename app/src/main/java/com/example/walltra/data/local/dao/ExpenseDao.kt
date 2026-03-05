package com.example.walltra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.walltra.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date = :date")
    fun getExpensesByDate(date: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getExpensesBetweenDates(startDate: String, endDate: String): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :date")
    fun getTotalAmountByDate(date: String): Flow<Double?>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate AND categoryId = :categoryId")
    fun getExpensesByCategoryAndDateRange(
        categoryId: String,
        startDate: String,
        endDate: String
    ): Flow<List<Expense>>
}