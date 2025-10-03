package com.example.personalfinancetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.database.AppDatabase
import com.example.personalfinancetracker.data.entity.Expense
import com.example.personalfinancetracker.data.entity.TransactionType
import com.example.personalfinancetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository
    val allExpenses: LiveData<List<Expense>>

    init {
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)
        allExpenses = repository.allExpenses
    }

    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun update(expense: Expense) = viewModelScope.launch {
        repository.update(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }

    // Helper function to get current month's expenses
    fun getCurrentMonthExpenses(): LiveData<List<Expense>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        val endDate = calendar.timeInMillis

        return repository.getExpensesByDateRange(startDate, endDate)
    }

    // Calculate total for expenses or income
    fun calculateTotal(expenses: List<Expense>, type: TransactionType): Double {
        return expenses.filter { it.type == type }.sumOf { it.amount }
    }

    // Get expenses grouped by category
    fun getExpensesByCategory(expenses: List<Expense>): Map<String, Double> {
        return expenses
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }
}