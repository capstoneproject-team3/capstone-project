package com.example.personalfinancetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.personalfinancetracker.data.dao.CategoryDao
import com.example.personalfinancetracker.data.dao.ExpenseDao
import com.example.personalfinancetracker.data.entity.dao.BillDao
import com.example.personalfinancetracker.data.entity.Bill
import com.example.personalfinancetracker.data.entity.Category
import com.example.personalfinancetracker.data.entity.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Expense::class, Bill::class, Category::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun billDao(): BillDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_tracker_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDefaultCategories(database.categoryDao())
                    }
                }
            }
        }

        private suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
            val defaultCategories = listOf(
                Category(name = "Food", isDefault = true),
                Category(name = "Transport", isDefault = true),
                Category(name = "Bills", isDefault = true),
                Category(name = "Shopping", isDefault = true),
                Category(name = "Entertainment", isDefault = true),
                Category(name = "Healthcare", isDefault = true),
                Category(name = "Education", isDefault = true),
                Category(name = "Other", isDefault = true)
            )
            defaultCategories.forEach { categoryDao.insert(it) }
        }
    }
}