package com.example.walltra.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val name: String,
    val amount: Double,
    val date: String
)