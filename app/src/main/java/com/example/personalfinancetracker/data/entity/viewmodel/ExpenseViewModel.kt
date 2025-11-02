package com.example.personalfinancetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.database.AppDatabase
import com.example.personalfinancetracker.data.entity.Expense
import com.example.personalfinancetracker.data.entity.TransactionType
import com.example.personalfinancetracker.data.repository.ExpenseRepository
import com.example.personalfinancetracker.utils.ReportTimeCalculator // 👈 NEW IMPORT
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository
    val allExpenses: LiveData<List<Expense>>

    // Get the date ranges from the calculator (Initialize here for clean LiveData setup)
    private val monthRange = ReportTimeCalculator.getCurrentMonthRange()
    private val quarterRange = ReportTimeCalculator.getCurrentQuarterRange()
    private val yearRange = ReportTimeCalculator.getCurrentYearRange()

    init {
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        // Ensure repository is using the correct path from your earlier shared code
        repository = ExpenseRepository(expenseDao)
        allExpenses = repository.allExpenses
    }

    // --- SPRINT 2: REPORTING LIVE DATA ---

    // 1. Monthly Totals
    val totalMonthlyExpense: LiveData<Double?> =
        repository.getTotalAmountByDateRange(
            monthRange.first,
            monthRange.second,
            TransactionType.EXPENSE.name
        )
    val totalMonthlyIncome: LiveData<Double?> =
        repository.getTotalAmountByDateRange(
            monthRange.first,
            monthRange.second,
            TransactionType.INCOME.name
        )

    // 2. Quarterly Totals
    val totalQuarterlyExpense: LiveData<Double?> =
        repository.getTotalAmountByDateRange(
            quarterRange.first,
            quarterRange.second,
            TransactionType.EXPENSE.name
        )
    val totalQuarterlyIncome: LiveData<Double?> =
        repository.getTotalAmountByDateRange(
            quarterRange.first,
            quarterRange.second,
            TransactionType.INCOME.name
        )

    // 3. Yearly Totals
    val totalYearlyExpense: LiveData<Double?> =
        repository.getTotalAmountByDateRange(
            yearRange.first,
            yearRange.second,
            TransactionType.EXPENSE.name
        )
    val totalYearlyIncome: LiveData<Double?> =
        repository.getTotalAmountByDateRange(
            yearRange.first,
            yearRange.second,
            TransactionType.INCOME.name
        )

    // --- EXISTING CRUD FUNCTIONS ---
    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun update(expense: Expense) = viewModelScope.launch {
        repository.update(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }
    fun getExpenseById(id: Long): LiveData<Expense?> {
        return repository.getExpenseById(id)
    }

    // --- EXISTING HELPER FUNCTIONS ---

    // IMPORTANT: This function is now slightly redundant as you have 'getCurrentMonthExpenses'
    // in the LiveData above, but we keep it for MainActivity compatibility.
    fun getCurrentMonthExpenses(): LiveData<List<Expense>> {
        // We now use the calculator utility for this range definition
        val (startDate, endDate) = ReportTimeCalculator.getCurrentMonthRange()
        return repository.getExpensesByDateRange(startDate, endDate)
    }

    // Calculate total for expenses or income (Used by MainActivity UI)
    fun calculateTotal(expenses: List<Expense>, type: TransactionType): Double {
        return expenses.filter { it.type == type }.sumOf { it.amount }
    }

    // Get expenses grouped by category (Used by MainActivity UI)
    fun getExpensesByCategory(expenses: List<Expense>): Map<String, Double> {
        return expenses
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }
}