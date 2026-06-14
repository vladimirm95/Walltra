package com.example.walltra.ui.categories

import com.example.walltra.data.model.Category

data class CategoriesState(
    val categories: List<Category> = emptyList(),
    val categoryTotals: Map<String, Double> = emptyMap(),
    val error: String? = null
)