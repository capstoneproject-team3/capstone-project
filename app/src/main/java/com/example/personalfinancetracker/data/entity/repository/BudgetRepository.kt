package com.example.personalfinancetracker.data.entity.repository

import androidx.lifecycle.LiveData
import com.example.personalfinancetracker.data.entity.Budget
import com.example.personalfinancetracker.data.entity.dao.BudgetDao

class BudgetRepository(private val budgetDao: BudgetDao) {

    fun getAllBudgets(): LiveData<List<Budget>> = budgetDao.getAllBudgets()

    fun getBudgetsForMonth(month: Int, year: Int): LiveData<List<Budget>> =
        budgetDao.getBudgetsForMonth(month, year)

    suspend fun insert(budget: Budget): Long = budgetDao.insert(budget)

    suspend fun update(budget: Budget) = budgetDao.update(budget)

    suspend fun delete(budget: Budget) = budgetDao.delete(budget)

    suspend fun getLatestBudget(): Budget? = budgetDao.getLatestBudget()

    suspend fun deleteBudgetsForMonth(month: Int, year: Int) =
        budgetDao.deleteBudgetsForMonth(month, year)
}