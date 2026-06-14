package com.example.walltra.ui.categories

import com.example.walltra.data.model.Category

sealed class CategoriesIntent {
    data class AddCategory(val name: String) : CategoriesIntent()
    data class DeleteCategory(val category: Category) : CategoriesIntent()
}