package com.example.walltra.di

import com.example.walltra.data.repository.CategoryRepository
import com.example.walltra.data.repository.ExpenseRepository
import com.example.walltra.data.repository.PeriodRepository
import com.example.walltra.data.repository.impl.CategoryRepositoryImpl
import com.example.walltra.data.repository.impl.ExpenseRepositoryImpl
import com.example.walltra.data.repository.impl.PeriodRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        impl: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindPeriodRepository(
        impl: PeriodRepositoryImpl
    ): PeriodRepository
}