package com.example.walltra.data.repository.impl

import com.example.walltra.data.local.dao.PeriodDao
import com.example.walltra.data.model.Period
import com.example.walltra.data.repository.PeriodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeriodRepositoryImpl @Inject constructor(
    private val periodDao: PeriodDao
) : PeriodRepository {

    override fun getAllPeriods(): Flow<List<Period>> {
        return periodDao.getAllPeriods()
    }

    override suspend fun getPeriodById(id: String): Period? {
        return periodDao.getPeriodById(id)
    }

    override suspend fun insert(period: Period) {
        periodDao.insert(period)
    }

    override suspend fun delete(period: Period) {
        periodDao.delete(period)
    }
}