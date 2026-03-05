package com.example.walltra.data.repository

import com.example.walltra.data.model.Period
import kotlinx.coroutines.flow.Flow

interface PeriodRepository {
    fun getAllPeriods(): Flow<List<Period>>
    suspend fun getPeriodById(id: String): Period?
    suspend fun insert(period: Period)
    suspend fun delete(period: Period)
}