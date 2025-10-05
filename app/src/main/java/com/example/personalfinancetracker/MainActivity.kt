package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.data.entity.TransactionType
import com.example.personalfinancetracker.ui.home.CategoryAdapter
import com.example.personalfinancetracker.ui.home.ExpenseAdapter
import com.example.personalfinancetracker.viewmodel.ExpenseViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Color

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "CA"))
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup status bar - light icons for dark toolbar
        setupStatusBar()

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Setup UI
        setupRecyclerViews()
        setupFAB()
        observeData()
        updateMonthDisplay()
    }

    private fun setupStatusBar() {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, // Enable light status bar (dark icons)
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
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
            // Open Add Expense Activity
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        viewModel.getCurrentMonthExpenses().observe(this) { expenses ->
            // Update recent transactions (show all)
            expenseAdapter.updateExpenses(expenses)

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
}