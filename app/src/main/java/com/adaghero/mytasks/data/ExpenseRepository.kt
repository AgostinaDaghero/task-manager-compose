package com.adaghero.mytasks.data

import android.content.Context
import com.adaghero.mytasks.model.Expense
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

//Repository responsible for storing and loading expenses
class ExpenseRepository(private val context: Context) {
    private val gson = Gson()
    private val fileName = "expenses.json"

    //Holds in-memory list of expenses
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    init {
        loadFromFile()
    }

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
        file.writeText(gson.toJson(_expenses.value))
    }

    private fun loadFromFile() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            val listType = object : TypeToken<List<Expense>>() {}.type
            val loaded: List<Expense> = gson.fromJson(file.readText(), listType)
            _expenses.value = loaded
        }
    }
}