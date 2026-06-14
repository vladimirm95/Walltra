package com.example.walltra.ui.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walltra.data.model.Period
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    navController: NavController,
    viewModel: CompareViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val displayFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Poređenje perioda") },
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

                state.periods.size < 2 -> {
                    Text(
                        text = "Potrebna su bar dva sačuvana perioda za poređenje",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp)
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            PeriodDropdown(
                                label = "Period A",
                                periods = state.periods,
                                selected = state.periodA,
                                onSelect = { viewModel.onIntent(CompareIntent.SelectPeriodA(it)) },
                                displayFormatter = displayFormatter,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            PeriodDropdown(
                                label = "Period B",
                                periods = state.periods,
                                selected = state.periodB,
                                onSelect = { viewModel.onIntent(CompareIntent.SelectPeriodB(it)) },
                                displayFormatter = displayFormatter,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val comparisons = state.categories.mapNotNull { category ->
                            val a = state.expensesA.filter { it.categoryId == category.id }.sumOf { it.amount }
                            val b = state.expensesB.filter { it.categoryId == category.id }.sumOf { it.amount }
                            if (a > 0 || b > 0) {
                                CategoryComparison(category.name, a.toFloat(), b.toFloat())
                            } else null
                        }.sortedByDescending { it.valueA + it.valueB }

                        if (comparisons.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nema podataka za poređenje",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            val totalA = state.expensesA.sumOf { it.amount }.toFloat()
                            val totalB = state.expensesB.sumOf { it.amount }.toFloat()

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                item {
                                    Column {
                                        CategoryRatioRow(
                                            label = "Ukupno",
                                            valueA = totalA,
                                            valueB = totalB,
                                            emphasized = true
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        HorizontalDivider()
                                    }
                                }

                                itemsIndexed(comparisons, key = { _, item -> item.categoryName }) { index, comparison ->
                                    CategoryRatioRow(
                                        label = comparison.categoryName,
                                        valueA = comparison.valueA,
                                        valueB = comparison.valueB,
                                        highlight = index == 0
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

private data class CategoryComparison(
    val categoryName: String,
    val valueA: Float,
    val valueB: Float
)

@Composable
private fun CategoryRatioRow(
    label: String,
    valueA: Float,
    valueB: Float,
    emphasized: Boolean = false,
    highlight: Boolean = false
) {
    val total = (valueA + valueB).coerceAtLeast(0f)
    val fractionA = if (total > 0f) (valueA / total).coerceIn(0f, 1f) else 0.5f
    val fractionB = 1f - fractionA

    val darkRed = Color(0xFFA32D2D)
    val lightRed = Color(0xFFF09595)

    val colorA = if (valueA >= valueB) darkRed else lightRed
    val colorB = if (valueA >= valueB) lightRed else darkRed

    val labelStyle = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall
    val valueStyle = if (emphasized) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium
    val barHeight = if (emphasized) 6.dp else 3.dp
    val fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = label, style = labelStyle, fontWeight = fontWeight)

            if (highlight) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(darkRed.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "najveći trošak",
                        style = MaterialTheme.typography.labelSmall,
                        color = darkRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            if (fractionA > 0f) {
                Box(
                    modifier = Modifier
                        .weight(fractionA)
                        .fillMaxHeight()
                        .background(colorA)
                )
            }
            if (fractionB > 0f) {
                Box(
                    modifier = Modifier
                        .weight(fractionB)
                        .fillMaxHeight()
                        .background(colorB)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "%.0f".format(valueA), style = valueStyle, fontWeight = fontWeight)
            Text(text = "%.0f".format(valueB), style = valueStyle, fontWeight = fontWeight)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodDropdown(
    label: String,
    periods: List<Period>,
    selected: Period?,
    onSelect: (Period) -> Unit,
    displayFormatter: DateTimeFormatter,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    fun periodLabel(period: Period): String {
        val start = LocalDate.parse(period.startDate).format(displayFormatter)
        val end = LocalDate.parse(period.endDate).format(displayFormatter)
        return "$start - $end"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected?.let { periodLabel(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            periods.forEach { period ->
                DropdownMenuItem(
                    text = { Text(periodLabel(period)) },
                    onClick = {
                        onSelect(period)
                        expanded = false
                    }
                )
            }
        }
    }
}