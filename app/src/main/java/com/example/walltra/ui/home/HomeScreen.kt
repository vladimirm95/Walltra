package com.example.walltra.ui.home

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walltra.navigation.Screen
import com.example.walltra.ui.common.LocalCurrency
import com.example.walltra.ui.common.formatAmount
import com.example.walltra.ui.home.components.AddExpenseDialog
import com.example.walltra.ui.home.components.CalendarView
import com.example.walltra.ui.home.components.CategoryBreakdownSheet
import com.example.walltra.ui.home.components.DonutChart
import com.example.walltra.ui.home.components.DonutSlice
import java.time.format.DateTimeFormatter

val categoryColors = listOf(
    androidx.compose.ui.graphics.Color(0xFF4CAF50),
    androidx.compose.ui.graphics.Color(0xFF2196F3),
    androidx.compose.ui.graphics.Color(0xFFFFC107),
    androidx.compose.ui.graphics.Color(0xFFF44336),
    androidx.compose.ui.graphics.Color(0xFF9C27B0),
    androidx.compose.ui.graphics.Color(0xFF00BCD4),
    androidx.compose.ui.graphics.Color(0xFFFF9800),
    androidx.compose.ui.graphics.Color(0xFF607D8B)
)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var selectedCategoryIndex by remember { mutableStateOf<Int?>(null) }
    val currency = LocalCurrency.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = {
                showSaveDialog = false
                viewModel.onIntent(HomeIntent.DismissSavePeriod)
            },
            title = { Text("Sačuvaj period") },
            text = {
                val startDate = state.currentPeriodStartDate?.format(displayFormatter) ?: ""
                val endDate = java.time.LocalDate.now().format(displayFormatter)
                Text("Da li želite da sačuvate period $startDate - $endDate?")
            },
            confirmButton = {
                Button(onClick = {
                    showSaveDialog = false
                    viewModel.onIntent(HomeIntent.ConfirmSavePeriod)
                }) {
                    Text("Potvrdi")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showSaveDialog = false
                    viewModel.onIntent(HomeIntent.DismissSavePeriod)
                }) {
                    Text("Otkaži")
                }
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            categories = state.categories,
            selectedDate = state.selectedDate,
            onConfirm = { name, amount, categoryId ->
                viewModel.onIntent(HomeIntent.AddExpense(name, amount, categoryId))
                showAddExpenseDialog = false
            },
            onDismiss = { showAddExpenseDialog = false }
        )
    }

    selectedCategoryIndex?.let { index ->
        val category = state.categories[index]
        val categoryExpenses = state.expenses.filter { it.categoryId == category.id }
        CategoryBreakdownSheet(
            category = category,
            expenses = categoryExpenses,
            color = categoryColors[index % categoryColors.size],
            currency = currency,
            onDismiss = { selectedCategoryIndex = null }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddExpenseDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj trošak")
            }
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.Periods) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Periodi")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { navController.navigate(Screen.Compare) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Poređenje")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sačuvaj")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // TODO: privremeno - ukloni nakon testiranja
                TextButton(onClick = { viewModel.onIntent(HomeIntent.SeedMockData) }) {
                    Text("🔧 Generiši test podatke")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Period header + podešavanja
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    state.currentPeriodStartDate?.let { startDate ->
                        Text(
                            text = "Period od ${startDate.format(displayFormatter)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { navController.navigate(Screen.Settings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Podešavanja")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Donut chart i legenda - pola ekrana
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DonutChart(
                        slices = slices,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        state.categories.forEachIndexed { index, category ->
                            val total = state.expenses
                                .filter { it.categoryId == category.id }
                                .sumOf { it.amount }
                            if (total > 0) {
                                Text(
                                    text = "• ${category.name}: ${total.formatAmount(currency)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.clickable {
                                        selectedCategoryIndex = index
                                    }
                                )
                            }
                        }

                        val grandTotal = state.expenses.sumOf { it.amount }
                        if (grandTotal > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ukupno: ${grandTotal.formatAmount(currency)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Kalendar - pola ekrana, prilepljen uz dugmad
                CalendarView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    currentMonth = state.currentMonth,
                    selectedDate = state.selectedDate,
                    periodStartDate = state.currentPeriodStartDate,
                    totalAmountByDate = state.totalAmountByDate,
                    onDayClick = { date ->
                        viewModel.onIntent(HomeIntent.SelectDate(date))
                        navController.navigate(Screen.Day(date.format(formatter)))
                    },
                    onMonthChange = { month ->
                        viewModel.onIntent(HomeIntent.SetMonth(month))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}