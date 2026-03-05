package com.example.walltra.data.repository

import com.example.walltra.data.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: String): Category?
    suspend fun insert(category: Category)
    suspend fun insertAll(categories: List<Category>)
}