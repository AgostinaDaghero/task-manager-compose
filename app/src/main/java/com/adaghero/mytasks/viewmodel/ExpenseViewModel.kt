package com.adaghero.mytasks.viewmodel

import com.adaghero.mytasks.data.ExpenseRepository
import androidx.lifecycle.viewModelScope
import com.adaghero.mytasks.model.Expense
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import android.app.Application
import androidx.lifecycle.AndroidViewModel

//ViewModel for expenses, mediates between UI and repository
class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository(application.applicationContext)

    //Stream of all expenses
    val expenses: StateFlow<List<Expense>> = repository.expenses
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    //Reactive balance: updates automatically when expenses change
    val balance: StateFlow<Double> = repository.expenses
        .map { expenses ->
            val income = expenses.filter { it.type.name == "INCOME" }.sumOf { it.amount }
            val expense = expenses.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }
            income - expense
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    //Add a new expense
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.addExpense(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }
}
