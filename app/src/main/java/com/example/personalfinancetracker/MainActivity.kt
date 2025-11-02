package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.view.WindowInsetsController
import android.widget.TextView
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
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity() {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val currencyFormat = NumberFormat.getCurrencyInstance(
        Locale.Builder()
            .setLanguage("en")
            .setRegion("CA")
            .build()
    )
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, R.string.notification_permission_granted, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, R.string.notification_permission_denied, Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupStatusBar()
        requestNotificationPermission()

        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        setupRecyclerViews()
        setupFAB()
        setupBillsButton()
        setupReportsButton()
        setupBottomNavigation(
            findViewById(R.id.bottomNavigation),
            R.id.navigation_home
        )

        observeData()
        updateMonthDisplay()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    private fun setupRecyclerViews() {
        val rvRecentTransactions = findViewById<RecyclerView>(R.id.rvRecentTransactions)
        expenseAdapter = ExpenseAdapter(
            emptyList(),
            onEditClick = { expense ->
                val intent = Intent(this, AddExpenseActivity::class.java)
                intent.putExtra("EXPENSE_ID", expense.id)
                startActivity(intent)
            },
            onDeleteClick = { expense ->
                viewModel.delete(expense)
                Toast.makeText(this, R.string.transaction_deleted, Toast.LENGTH_SHORT).show()
            }
        )
        rvRecentTransactions.adapter = expenseAdapter
        rvRecentTransactions.layoutManager = LinearLayoutManager(this)

        val rvTopCategories = findViewById<RecyclerView>(R.id.rvTopCategories)
        categoryAdapter = CategoryAdapter(emptyMap())
        rvTopCategories.adapter = categoryAdapter
        rvTopCategories.layoutManager = LinearLayoutManager(this)
    }

    private fun setupReportsButton() {
        val btnReports = findViewById<android.widget.Button>(R.id.btnReports)
        btnReports.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBillsButton() {
        val btnBills = findViewById<android.widget.Button>(R.id.btnBills)
        btnBills.setOnClickListener {
            val intent = Intent(this, BillListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupFAB() {
        val fab = findViewById<FloatingActionButton>(R.id.fabAddExpense)
        fab.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        viewModel.getCurrentMonthExpenses().observe(this) { expenses ->
            expenseAdapter.updateExpenses(expenses)

            val totalExpense = viewModel.calculateTotal(expenses, TransactionType.EXPENSE)
            val totalIncome = viewModel.calculateTotal(expenses, TransactionType.INCOME)

            findViewById<TextView>(R.id.tvTotalExpense).text = currencyFormat.format(totalExpense)
            findViewById<TextView>(R.id.tvTotalIncome).text =
                getString(R.string.income_label, currencyFormat.format(totalIncome))

            val categoriesMap = viewModel.getExpensesByCategory(expenses)
            categoryAdapter.updateCategories(categoriesMap)
        }
    }

    private fun updateMonthDisplay() {
        val currentDate = Date()
        findViewById<TextView>(R.id.tvCurrentMonth).text = monthFormat.format(currentDate)
    }
}
