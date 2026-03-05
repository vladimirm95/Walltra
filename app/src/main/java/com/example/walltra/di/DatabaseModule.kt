package com.example.walltra.di

import android.content.Context
import androidx.room.Room
import com.example.walltra.data.local.WalltraDatabase
import com.example.walltra.data.local.dao.CategoryDao
import com.example.walltra.data.local.dao.ExpenseDao
import com.example.walltra.data.local.dao.PeriodDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WalltraDatabase {
        return WalltraDatabase.create(context)
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: WalltraDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: WalltraDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    @Singleton
    fun providePeriodDao(database: WalltraDatabase): PeriodDao {
        return database.periodDao()
    }
}