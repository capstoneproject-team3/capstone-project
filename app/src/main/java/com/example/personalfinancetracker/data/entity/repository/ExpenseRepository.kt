package com.example.personalfinancetracker.data.repository

import androidx.lifecycle.LiveData
import com.example.personalfinancetracker.data.dao.ExpenseDao
import com.example.personalfinancetracker.data.entity.Expense

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun update(expense: Expense) {
        expenseDao.update(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }

    suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)
    }

    fun getExpensesByDateRange(startDate: Long, endDate: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesByDateRange(startDate, endDate)
    }

    // 👇 NEW FOR REPORTING: Expose the DAO's total amount calculation
    fun getTotalAmountByDateRange(startDate: Long, endDate: Long, transactionType: String): LiveData<Double?> {
        return expenseDao.getTotalAmountByDateRange(startDate, endDate, transactionType)
    }
    // 👆 END NEW
}