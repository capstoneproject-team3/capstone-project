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

// Notification permission imports
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "CA"))
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    // Register the permissions callback
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notification permission granted. Reminders will work!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Notification permission denied. Reminders may not appear.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup status bar - light icons for dark toolbar
        setupStatusBar()

        // Check and request POST_NOTIFICATIONS permission
        requestNotificationPermission()

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Setup UI
        setupRecyclerViews()
        setupFAB()
//        setupBillsButton()
        setupRecentTransactionsNavigation()

        observeData()
        updateMonthDisplay()
    }

    // Handle permission request for Android 13 (API 33) and above
    private fun requestNotificationPermission() {
        // Check if the device runs Android 13 (API 33) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if permission is NOT granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission using the launcher
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupStatusBar() {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
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
            // When an item is clicked, open TransactionListActivity for edit/delete
            val intent = Intent(this, TransactionListActivity::class.java)
            startActivity(intent)
        }
        rvRecentTransactions.adapter = expenseAdapter
        rvRecentTransactions.layoutManager = LinearLayoutManager(this)

        // Top Categories RecyclerView
        val rvTopCategories = findViewById<RecyclerView>(R.id.rvTopCategories)
        categoryAdapter = CategoryAdapter(emptyMap())
        rvTopCategories.adapter = categoryAdapter
        rvTopCategories.layoutManager = LinearLayoutManager(this)
    }

    private fun setupRecentTransactionsNavigation() {
        // Option 1: If you have a "View All" TextView in your layout
        val tvViewAll = findViewById<TextView>(R.id.tvViewAllTransactions)
        tvViewAll?.setOnClickListener {
            val intent = Intent(this, TransactionListActivity::class.java)
            startActivity(intent)
        }

        // Option 2: Make the Recent Transactions header clickable
        val tvRecentTransactions = findViewById<TextView>(R.id.tvRecentTransactionsHeader)
        tvRecentTransactions?.setOnClickListener {
            val intent = Intent(this, TransactionListActivity::class.java)
            startActivity(intent)
        }

        // Option 3: Add a click listener to the entire Recent Transactions card
        val recentTransactionsCard = findViewById<android.view.View>(R.id.recentTransactionsCard)
        recentTransactionsCard?.setOnClickListener {
            val intent = Intent(this, TransactionListActivity::class.java)
            startActivity(intent)
        }
    }

//    private fun setupBillsButton() {
//        // Use safe call operator to prevent crash if button doesn't exist
//        val btnBills = findViewById<android.widget.Button>(R.id.btnBills)
//        btnBills?.setOnClickListener {
//            // Open the new Bill List Activity
//            val intent = Intent(this, BillListActivity::class.java)
//            startActivity(intent)
//        }
//    }

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
            // Update recent transactions (show only last 5 for preview)
            val recentExpenses = expenses.take(5)
            expenseAdapter.updateExpenses(recentExpenses)

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