package com.adaghero.mytasks.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adaghero.mytasks.model.Category
import com.adaghero.mytasks.model.Expense
import com.adaghero.mytasks.model.ExpenseType
import com.adaghero.mytasks.viewmodel.ExpenseViewModel

@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel) {
    val expenses by viewModel.expenses.collectAsState()
    val balance by viewModel.balance.collectAsState()
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ExpenseType.EXPENSE) }
    var category by remember { mutableStateOf(Category.OTHER) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Balance: $balance", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        //Form for adding expense/income
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })

        Row {
            Button(onClick = { type = ExpenseType.INCOME }) {
                Text("Income")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { type = ExpenseType.EXPENSE }) {
                Text("Expense")
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            if (amount.isNotBlank()) {
                viewModel.addExpense(
                    Expense(
                        title = title,
                        amount = amount.toDouble(),
                        type = type,
                        category = category
                    )
                )
                title = ""
                amount = ""
            }
        }) {
            Text("Add Transaction")
        }

        Spacer(Modifier.height(16.dp))
        Text("Transactions:", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(expenses) { exp ->
                Text("${exp.title} - ${exp.amount} (${exp.type})")
            }
        }
    }
}