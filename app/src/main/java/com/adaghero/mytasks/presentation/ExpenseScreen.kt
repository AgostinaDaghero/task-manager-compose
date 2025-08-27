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
import com.adaghero.mytasks.ui.theme.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background


@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel) {
    val expenses by viewModel.expenses.collectAsState()
    val balance by viewModel.balance.collectAsState()

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ExpenseType.EXPENSE) }
    var category by remember { mutableStateOf(Category.OTHER) }

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = "My Expenses",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text("Balance: $balance", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        PieChart(expenses = expenses, modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
        )

        Spacer(Modifier.height(16.dp))

        //Form for adding expense/income
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })

        Row(modifier = Modifier.padding(top = 8.dp)) {
            Button(onClick = { type = ExpenseType.INCOME },
                colors = ButtonDefaults.buttonColors(containerColor = Info)) {
                Text("Income")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { type = ExpenseType.EXPENSE },
                colors = ButtonDefaults.buttonColors(containerColor = HighPriority)) {
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
        },
            colors = ButtonDefaults.buttonColors(containerColor = Pink)
        ) {
            Text("Add Transaction")
        }

        Spacer(Modifier.height(16.dp))
        Text("Transactions:", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(expenses) { exp ->
                val color = when (exp.type){
                    ExpenseType.INCOME -> LowPriority
                    ExpenseType.EXPENSE -> HighPriority
                }
                Text("${exp.title} - ${exp.amount} (${exp.type})")
            }
        }
    }
}

@Composable
fun PieChart(expenses: List<Expense>, modifier: Modifier = Modifier) {
    val totalIncome = expenses.filter { it.type == ExpenseType.INCOME }.sumOf { it.amount }
    val totalExpense = expenses.filter { it.type == ExpenseType.EXPENSE }.sumOf { it.amount }
    val total = totalIncome + totalExpense

    if (total == 0.0) return

    val incomeAngle = (totalIncome / total * 360).toFloat()
    val expenseAngle = (totalExpense / total * 360).toFloat()


    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Canvas(modifier = modifier) {
            drawArc(
            color = LowPriority,
            startAngle = 0f,
            sweepAngle = incomeAngle,
            useCenter = true)
            drawArc(
            color = HighPriority,
            startAngle = incomeAngle,
            sweepAngle = expenseAngle,
            useCenter = true)
        }

        Spacer(modifier = Modifier.height(12.dp))

        //Caption below the graph
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Box(modifier = Modifier.size(16.dp).background(LowPriority))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Income")
            }
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Box(modifier = Modifier.size(16.dp).background(HighPriority))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Expense")
            }
        }
    }
}

