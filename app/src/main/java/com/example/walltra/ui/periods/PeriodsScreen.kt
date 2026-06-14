package com.example.walltra.ui.periods

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.walltra.data.model.Period
import com.example.walltra.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodsScreen(
    navController: NavController,
    viewModel: PeriodsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var periodToDelete by remember { mutableStateOf<Period?>(null) }
    val displayFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    periodToDelete?.let { period ->
        val start = LocalDate.parse(period.startDate).format(displayFormatter)
        val end = LocalDate.parse(period.endDate).format(displayFormatter)

        AlertDialog(
            onDismissRequest = { periodToDelete = null },
            title = { Text("Obriši period") },
            text = { Text("Da li želite da obrišete period $start - $end? Ovo neće obrisati unete troškove.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.onIntent(PeriodsIntent.DeletePeriod(period))
                    periodToDelete = null
                }) {
                    Text("Obriši")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { periodToDelete = null }) {
                    Text("Otkaži")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Periodi") },
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

                state.periods.isEmpty() -> {
                    Text(
                        text = "Još nema sačuvanih perioda",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.periods,
                            key = { it.period.id }
                        ) { item ->
                            PeriodItem(
                                item = item,
                                onClick = {
                                    navController.navigate(Screen.PeriodDetail(item.period.id))
                                },
                                onDelete = { periodToDelete = item.period }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodItem(item: PeriodWithTotal, onClick: () -> Unit, onDelete: () -> Unit) {
    val displayFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")
    val start = LocalDate.parse(item.period.startDate).format(displayFormatter)
    val end = LocalDate.parse(item.period.endDate).format(displayFormatter)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$start - $end", style = MaterialTheme.typography.bodyLarge)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "%.0f".format(item.total),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Obriši period",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}