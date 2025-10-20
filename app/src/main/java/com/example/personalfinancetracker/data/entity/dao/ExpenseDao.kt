package com.example.personalfinancetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.personalfinancetracker.data.entity.Expense

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): LiveData<List<Expense>>

    // 👇 NEW FOR REPORTING: Get total amount by date range and type
    @Query("""
        SELECT SUM(amount) FROM expenses 
        WHERE date BETWEEN :startDate AND :endDate 
        AND type = :transactionType
    """)
    fun getTotalAmountByDateRange(startDate: Long, endDate: Long, transactionType: String): LiveData<Double?>
    // 👆 END NEW
}