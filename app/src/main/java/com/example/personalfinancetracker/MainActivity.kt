package com.example.personalfinancetracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.data.entity.Expense
import com.example.personalfinancetracker.data.entity.TransactionType
import com.example.personalfinancetracker.ui.home.CategoryAdapter
import com.example.personalfinancetracker.ui.home.ExpenseAdapter
import com.example.personalfinancetracker.viewmodel.ExpenseViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Setup UI
        setupRecyclerViews()
        setupFAB()
        observeData()
        updateMonthDisplay()
    }

    private fun setupRecyclerViews() {
        // Recent Transactions RecyclerView
        val rvRecentTransactions = findViewById<RecyclerView>(R.id.rvRecentTransactions)
        expenseAdapter = ExpenseAdapter(emptyList()) { expense ->
            // Handle item click (we'll add edit/delete functionality later)
        }
        rvRecentTransactions.adapter = expenseAdapter
        rvRecentTransactions.layoutManager = LinearLayoutManager(this)

        // Top Categories RecyclerView
        val rvTopCategories = findViewById<RecyclerView>(R.id.rvTopCategories)
        categoryAdapter = CategoryAdapter(emptyMap())
        rvTopCategories.adapter = categoryAdapter
        rvTopCategories.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFAB() {
        val fab = findViewById<FloatingActionButton>(R.id.fabAddExpense)
        fab.setOnClickListener {
            // TODO: Open Add Expense Activity
            // For now, let's add sample expenses
            addSampleExpenses()
        }
    }

    private fun observeData() {
        viewModel.getCurrentMonthExpenses().observe(this) { expenses ->
            // Update recent transactions (show last 5)
            expenseAdapter.updateExpenses(expenses.take(5))

            // Calculate and display totals
            val totalExpense = viewModel.calculateTotal(expenses, TransactionType.EXPENSE)
            val totalIncome = viewModel.calculateTotal(expenses, TransactionType.INCOME)

            findViewById<TextView>(R.id.tvTotalExpense).text = currencyFormat.format(totalExpense)
            findViewById<TextView>(R.id.tvTotalIncome).text =
                "Income: ${currencyFormat.format(totalIncome)}"

            // Update top categories
            val categoriesMap = viewModel.getExpensesByCategory(expenses)
            categoryAdapter.updateCategories(categoriesMap)
        }
    }

    private fun updateMonthDisplay() {
        val currentDate = Date()
        findViewById<TextView>(R.id.tvCurrentMonth).text = monthFormat.format(currentDate)
    }

    // Temporary function to add sample data for testing
    private fun addSampleExpenses() {
        val sampleExpenses = listOf(
            Expense(
                expenseName = "Lunch at Restaurant",
                amount = 450.0,
                category = "Food",
                date = System.currentTimeMillis(),
                description = "Lunch with colleagues",
                type = TransactionType.EXPENSE,
                isRecurring = false
            ),
            Expense(
                expenseName = "Uber Ride",
                amount = 180.0,
                category = "Transport",
                date = System.currentTimeMillis() - 86400000, // Yesterday
                description = "Ride to office",
                type = TransactionType.EXPENSE,
                isRecurring = false
            ),
            Expense(
                expenseName = "Grocery Shopping",
                amount = 2500.0,
                category = "Shopping",
                date = System.currentTimeMillis() - 172800000, // 2 days ago
                description = "Weekly groceries",
                type = TransactionType.EXPENSE,
                isRecurring = false
            ),
            Expense(
                expenseName = "Electricity Bill",
                amount = 1200.0,
                category = "Bills",
                date = System.currentTimeMillis() - 259200000, // 3 days ago
                description = "Monthly electricity",
                type = TransactionType.EXPENSE,
                isRecurring = true,
                recurrencePeriod = com.example.personalfinancetracker.data.entity.RecurrencePeriod.MONTHLY
            ),
            Expense(
                expenseName = "Salary",
                amount = 50000.0,
                category = "Salary",
                date = System.currentTimeMillis() - 345600000, // 4 days ago
                description = "Monthly salary",
                type = TransactionType.INCOME,
                isRecurring = true,
                recurrencePeriod = com.example.personalfinancetracker.data.entity.RecurrencePeriod.MONTHLY
            )
        )

        sampleExpenses.forEach { expense ->
            viewModel.insert(expense)
        }
    }
}