package com.example.personalfinancetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.database.AppDatabase
import com.example.personalfinancetracker.data.entity.Budget
import com.example.personalfinancetracker.data.entity.repository.BudgetRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BudgetRepository
    val allBudgets: LiveData<List<Budget>>

    init {
        val budgetDao = AppDatabase.getDatabase(application).budgetDao()
        repository = BudgetRepository(budgetDao)
        allBudgets = repository.getAllBudgets()
    }

    fun getBudgetsForMonth(month: Int, year: Int): LiveData<List<Budget>> {
        return repository.getBudgetsForMonth(month, year)
    }

    fun getCurrentMonthBudgets(): LiveData<List<Budget>> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)
        return repository.getBudgetsForMonth(currentMonth, currentYear)
    }

    fun insert(budget: Budget) = viewModelScope.launch {
        repository.insert(budget)
    }

    fun update(budget: Budget) = viewModelScope.launch {
        repository.update(budget)
    }

    fun delete(budget: Budget) = viewModelScope.launch {
        repository.delete(budget)
    }

    suspend fun getLatestBudget(): Budget? {
        return repository.getLatestBudget()
    }

    fun deleteBudgetsForMonth(month: Int, year: Int) = viewModelScope.launch {
        repository.deleteBudgetsForMonth(month, year)
    }
}