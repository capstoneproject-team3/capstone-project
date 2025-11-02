package com.example.personalfinancetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false, // To prevent deletion of default categories
    val createdAt: Long = System.currentTimeMillis()
)