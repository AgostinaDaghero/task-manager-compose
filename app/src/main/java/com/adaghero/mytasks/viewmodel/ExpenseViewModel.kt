package com.adaghero.mytasks.viewmodel

import com.adaghero.mytasks.data.ExpenseRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adaghero.mytasks.model.Expense
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import android.content.Context

//ViewModel for expenses, mediates between UI and repository
class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel(){

    //Stream of all expenses
    val expenses: StateFlow<List<Expense>> = repository.expenses
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    //Reactive balance: updates automatically when expenses change
    val balance: StateFlow<Double> = repository.expenses
        .map { repository.getBalance() }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    //Add a new expense
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.addExpense(expense)
        }
    }
}

//Factory to create ViewModel with context
class ExpenseViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(ExpenseRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}