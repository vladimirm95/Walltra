package com.example.walltra.data.repository.impl

import com.example.walltra.data.local.dao.CategoryDao
import com.example.walltra.data.model.Category
import com.example.walltra.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }

    override suspend fun getCategoryById(id: String): Category? {
        return categoryDao.getCategoryById(id)
    }

    override suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    override suspend fun insertAll(categories: List<Category>) {
        categoryDao.insertAll(categories)
    }
}