package com.adaghero.mytasks.data

import android.content.Context
import com.adaghero.mytasks.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

//Repository responsible for storing and loading expenses
class ExpenseRepository(private val context: Context) {
    private val fileName = "expenses.json"
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    //Holds in-memory list of expenses
    private val _expenses = MutableStateFlow<List<Expense>>(loadFromFile())
    val expenses: StateFlow<List<Expense>> = _expenses

    fun addExpense(expense: Expense) {
        _expenses.value = _expenses.value + expense
        saveToFile()
    }

    fun getBalance(): Double {
        val income = _expenses.value.filter { it.type.name == "INCOME" }.sumOf { it.amount }
        val expense = _expenses.value.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }
        return income - expense
    }

    private fun saveToFile() {
        val file = File(context.filesDir, fileName)
        file.writeText(json.encodeToString(_expenses.value))
    }

    private fun loadFromFile(): List<Expense> {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            try {
                json.decodeFromString<List<Expense>>(file.readText())
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}