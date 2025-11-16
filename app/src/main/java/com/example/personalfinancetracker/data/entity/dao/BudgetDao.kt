package com.example.personalfinancetracker.data.entity.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.personalfinancetracker.data.entity.Budget

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year ORDER BY createdAt DESC")
    fun getBudgetsForMonth(month: Int, year: Int): LiveData<List<Budget>>

    @Query("SELECT * FROM budgets ORDER BY year DESC, month DESC, createdAt DESC")
    fun getAllBudgets(): LiveData<List<Budget>>

    @Query("DELETE FROM budgets WHERE month = :month AND year = :year")
    suspend fun deleteBudgetsForMonth(month: Int, year: Int)

    @Query("SELECT * FROM budgets ORDER BY year DESC, month DESC LIMIT 1")
    suspend fun getLatestBudget(): Budget?
}