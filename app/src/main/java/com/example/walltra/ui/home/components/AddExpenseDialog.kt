package com.example.walltra.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.walltra.data.model.Category
import com.example.walltra.data.model.Expense
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    categories: List<Category>,
    selectedDate: LocalDate,
    initialExpense: Expense? = null,
    onConfirm: (name: String, amount: Double, categoryId: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialExpense?.name ?: "") }
    var amountText by remember {
        mutableStateOf(initialExpense?.amount?.let { formatAmountForEdit(it) } ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(
            categories.find { it.id == initialExpense?.categoryId } ?: categories.firstOrNull()
        )
    }
    var expanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val displayFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialExpense != null) "Izmeni trošak" else "Novi trošak") },
        text = {
            Column {
                Text(
                    text = "Datum: ${selectedDate.format(displayFormatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Naziv") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        amountText = input.filter { it.isDigit() || it == '.' || it == ',' }
                    },
                    label = { Text("Iznos") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategorija") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amount = amountText.replace(',', '.').toDoubleOrNull()
                val category = selectedCategory
                when {
                    name.isBlank() -> error = "Unesite naziv troška"
                    amount == null || amount <= 0 -> error = "Unesite ispravan iznos"
                    category == null -> error = "Izaberite kategoriju"
                    else -> onConfirm(name.trim(), amount, category.id)
                }
            }) {
                Text("Sačuvaj")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Otkaži")
            }
        }
    )
}

private fun formatAmountForEdit(amount: Double): String {
    return if (amount == amount.toLong().toDouble()) {
        amount.toLong().toString()
    } else {
        amount.toString()
    }
}