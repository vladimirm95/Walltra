package com.example.walltra.ui.categories.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walltra.data.model.Category
import com.example.walltra.ui.home.categoryColors

private data class CategoryBar(
    val name: String,
    val total: Double,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
fun CategoryTotalsChart(
    categories: List<Category>,
    categoryTotals: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val entries = remember(categories, categoryTotals) {
        categories.mapIndexedNotNull { index, category ->
            val total = categoryTotals[category.id] ?: 0.0
            if (total > 0) {
                CategoryBar(category.name, total, categoryColors[index % categoryColors.size])
            } else null
        }.sortedByDescending { it.total }
    }

    if (entries.isEmpty()) {
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                text = "Nema podataka za grafik",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    val maxValue = entries.first().total

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(entries, key = { it.name }) { entry ->
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(entry.name, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "%.0f".format(entry.total),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (entry.total / maxValue).toFloat().coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(entry.color, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}