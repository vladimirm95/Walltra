package com.example.walltra.ui.periods

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walltra.ui.home.categoryColors
import com.example.walltra.ui.home.components.DonutChart
import com.example.walltra.ui.home.components.DonutSlice
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodDetailScreen(
    navController: NavController,
    viewModel: PeriodDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val displayFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val period = state.period
                    if (period != null) {
                        val start = LocalDate.parse(period.startDate).format(displayFormatter)
                        val end = LocalDate.parse(period.endDate).format(displayFormatter)
                        Text("$start - $end")
                    } else {
                        Text("Period")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.expenses.isEmpty() -> {
                    Text(
                        text = "Nema troškova u ovom periodu",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        val slices = state.categories.mapIndexedNotNull { index, category ->
                            val total = state.expenses
                                .filter { it.categoryId == category.id }
                                .sumOf { it.amount }
                            if (total > 0) {
                                DonutSlice(
                                    value = total.toFloat(),
                                    color = categoryColors[index % categoryColors.size]
                                )
                            } else null
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DonutChart(
                                slices = slices,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                state.categories.forEach { category ->
                                    val total = state.expenses
                                        .filter { it.categoryId == category.id }
                                        .sumOf { it.amount }
                                    if (total > 0) {
                                        Text(
                                            text = "• ${category.name}: ${"%.0f".format(total)}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                val grandTotal = state.expenses.sumOf { it.amount }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ukupno: ${"%.0f".format(grandTotal)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Troškovi", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.expenses.sortedByDescending { it.date },
                                key = { it.id }
                            ) { expense ->
                                val categoryName = state.categories
                                    .find { it.id == expense.categoryId }
                                    ?.name ?: ""
                                val dateLabel = LocalDate.parse(expense.date).format(displayFormatter)

                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(expense.name, fontWeight = FontWeight.Medium)
                                            Text(
                                                text = "$categoryName • $dateLabel",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "%.0f".format(expense.amount),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}