package com.adaghero.mytasks.model

import kotlinx.serialization.Serializable
import java.util.UUID

//ExpenseType allows us to separate income and expense
enum class ExpenseType{
    INCOME, EXPENSE
}

//Category gives better tracking of spending sources
enum class Category{
    FOOD, TRANSPORT, ENTERTAINMENT, HEALTH, SALARY, OTHER
}

@Serializable
data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val type: ExpenseType,
    val category: Category,
    val timestamp: Long = System.currentTimeMillis()
)
