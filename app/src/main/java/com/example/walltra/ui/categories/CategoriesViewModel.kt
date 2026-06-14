package com.example.walltra.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walltra.data.model.Category
import com.example.walltra.data.repository.CategoryRepository
import com.example.walltra.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriesState())
    val state: StateFlow<CategoriesState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            expenseRepository.getTotalsByCategory().collect { totals ->
                _state.update {
                    it.copy(categoryTotals = totals.associate { t -> t.categoryId to t.total })
                }
            }
        }
    }

    fun onIntent(intent: CategoriesIntent) {
        when (intent) {
            is CategoriesIntent.AddCategory -> addCategory(intent.name)
            is CategoriesIntent.DeleteCategory -> deleteCategory(intent.category)
        }
    }

    private fun addCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            try {
                val category = Category(id = UUID.randomUUID().toString(), name = trimmed)
                categoryRepository.insert(category)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                val count = expenseRepository.countByCategory(category.id)
                if (count > 0) {
                    _state.update {
                        it.copy(error = "Kategorija \"${category.name}\" ima $count troškova i ne može se obrisati")
                    }
                } else {
                    categoryRepository.delete(category)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}