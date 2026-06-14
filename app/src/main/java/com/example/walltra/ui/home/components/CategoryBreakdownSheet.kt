package com.example.walltra.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walltra.data.model.Category
import com.example.walltra.data.model.Expense
import com.example.walltra.ui.common.formatAmount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBreakdownSheet(
    category: Category,
    expenses: List<Expense>,
    color: Color,
    currency: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            val total = expenses.sumOf { it.amount }

            Text(category.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
            Text(
                text = "Ukupno: ${total.formatAmount(currency)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            val grouped = expenses
                .groupBy { it.name.trim().lowercase() }
                .map { (key, items) ->
                    val displayName = key.replaceFirstChar { it.uppercase() }
                    displayName to items.sumOf { it.amount }
                }
                .sortedByDescending { it.second }

            val maxValue = grouped.maxOfOrNull { it.second } ?: 0.0

            grouped.forEach { (name, amount) ->
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = name, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = amount.formatAmount(currency),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    val fraction = if (maxValue > 0) (amount / maxValue).toFloat().coerceIn(0f, 1f) else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .background(color)
                        )
                    }
                }
            }
        }
    }
}