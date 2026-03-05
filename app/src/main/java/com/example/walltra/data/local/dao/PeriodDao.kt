package com.example.walltra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.example.walltra.data.model.Period
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(period: Period)

    @Delete
    suspend fun delete(period: Period)

    @Query("SELECT * FROM periods ORDER BY startDate DESC")
    fun getAllPeriods(): Flow<List<Period>>

    @Query("SELECT * FROM periods WHERE id = :id")
    suspend fun getPeriodById(id: String): Period?
}