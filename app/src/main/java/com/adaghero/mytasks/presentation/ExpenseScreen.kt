package com.adaghero.mytasks.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adaghero.mytasks.model.Expense
import com.adaghero.mytasks.model.ExpenseType
import com.adaghero.mytasks.viewmodel.ExpenseViewModel
import com.adaghero.mytasks.ui.theme.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.adaghero.mytasks.model.Category
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size



@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel) {
    val expenses by viewModel.expenses.collectAsState()
    val balance by viewModel.balance.collectAsState()
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ExpenseType.EXPENSE) }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "My Expenses",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            Text(
                "Balance: $balance",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    expenses = expenses,
                    modifier = Modifier.size(200.dp)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth()
                    )


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { type = ExpenseType.INCOME },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Income", color = Color.White)
                        }

                        Button(
                            onClick = { type = ExpenseType.EXPENSE },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Expense", color = Color.White)
                        }
                    }

                    Button(
                        onClick = {
                            if (amount.isNotBlank() && title.isNotBlank()) {
                                viewModel.addExpense(
                                    Expense(
                                        title = title,
                                        amount = amount.toDoubleOrNull() ?: 0.0,
                                        category = Category.GENERAL,
                                        type = type
                                    )
                                )
                                title = ""
                                amount = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Transaction", color = Color.White)
                    }
                }
            }
        }

        items(expenses) { exp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = exp.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = exp.type.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (exp.type == ExpenseType.INCOME) Color(0xFF22C55E) else Color(0xFFEF4444)
                        )
                    }
                    Text(
                        text = "${if (exp.type == ExpenseType.INCOME) "+" else "-"}$${exp.amount}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (exp.type == ExpenseType.INCOME) Color(0xFF22C55E) else Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

@Composable
fun PieChart(expenses: List<Expense>, modifier: Modifier = Modifier) {
    val totalIncome = expenses.filter { it.type == ExpenseType.INCOME }.sumOf { it.amount }
    val totalExpense = expenses.filter { it.type == ExpenseType.EXPENSE }.sumOf { it.amount }
    val total = totalIncome + totalExpense

    if (total == 0.0) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No data available", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFF22C55E))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Income")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFFEF4444))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Expense")
                }
            }
        }
    }
    return
}
    val incomeAngle = (totalIncome / total * 360).toFloat()
    val expenseAngle = (totalExpense / total * 360).toFloat()

    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Canvas(modifier = modifier
            .aspectRatio(1f)
        ) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2 - 8.dp.toPx()
            val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)

            drawCircle(
                color = Color.Gray.copy(alpha = 0.1f),
                radius = radius,
                center = center
            )
            drawArc(color = Color(0xFF22C55E), -90f, incomeAngle, true,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius),
                size = Size(radius * 2, radius * 2))
            drawArc(color = Color(0xFFEF4444), -90f + incomeAngle, expenseAngle, true,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2))
        }
        Spacer(modifier = Modifier.height(8.dp))

        //Caption below the graph
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFF22C55E)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Income")
            }
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFFEF4444)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Expense")
            }
        }
    }
}

