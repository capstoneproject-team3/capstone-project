package com.example.personalfinancetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val month: Int, // 1-12
    val year: Int,
    val createdAt: Long = System.currentTimeMillis()
)

// Data class to hold budget vs spending information
data class BudgetWithSpending(
    val budget: Budget,
    val spent: Double,
    val remaining: Double,
    val percentageUsed: Float
) {
    val isOverBudget: Boolean
        get() = spent > budget.amount
}