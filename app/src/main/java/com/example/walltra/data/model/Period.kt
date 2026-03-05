package com.example.walltra.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "periods")
data class Period(
    @PrimaryKey
    val id: String,
    val startDate: String,
    val endDate: String
)