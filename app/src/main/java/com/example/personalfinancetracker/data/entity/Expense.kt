package com.example.personalfinancetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val expenseName: String,
    val amount: Double,
    val category: String,
    val date: Long, // Store as timestamp (Long) for Room
    val description: String,
    val type: TransactionType,
    val isRecurring: Boolean = false,
    val recurrencePeriod: RecurrencePeriod? = null,
    val nextDueDate: Long? = null, // Timestamp
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class RecurrencePeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}