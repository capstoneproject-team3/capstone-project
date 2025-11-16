package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.personalfinancetracker.data.entity.TransactionType
import com.example.personalfinancetracker.viewmodel.BudgetViewModel
import com.example.personalfinancetracker.viewmodel.ExpenseViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.NumberFormat
import java.util.Calendar

class SettingsActivity : BaseActivity() {

    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var expenseViewModel: ExpenseViewModel
    private val currencyFormat = NumberFormat.getCurrencyInstance()

    private lateinit var toolbar: MaterialToolbar
    private lateinit var budgetProgressCard: MaterialCardView
    private lateinit var tvBudgetStatus: TextView
    private lateinit var tvBudgetDetails: TextView
    private lateinit var budgetProgressBar: LinearProgressIndicator
    private lateinit var tvNoBudgetInSettings: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Toolbar to match Transactions screen
        toolbar = findViewById(R.id.toolbarSettings)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.settings)

        // ViewModels
        budgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Bottom navigation (shared logic in BaseActivity)
        setupBottomNavigation(
            findViewById(R.id.bottomNavigation),
            R.id.navigation_settings
        )

        setupViews()
        setupClickListeners()
        observeBudgetData()
    }

    private fun setupViews() {
        budgetProgressCard = findViewById(R.id.budgetProgressCard)
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus)
        tvBudgetDetails = findViewById(R.id.tvBudgetDetails)
        budgetProgressBar = findViewById(R.id.budgetProgressBar)
        tvNoBudgetInSettings = findViewById(R.id.tvNoBudgetInSettings)
    }

    private fun observeBudgetData() {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        budgetViewModel.getBudgetsForMonth(currentMonth, currentYear).observe(this) { budgets ->
            if (budgets.isNotEmpty()) {
                val budget = budgets[0]

                expenseViewModel.getCurrentMonthExpenses().observe(this) { expenses ->
                    val totalSpent =
                        expenseViewModel.calculateTotal(expenses, TransactionType.EXPENSE)
                    updateBudgetDisplay(budget.amount, totalSpent)
                }
            } else {
                showNoBudget()
            }
        }
    }

    private fun updateBudgetDisplay(budgetAmount: Double, spent: Double) {
        budgetProgressCard.visibility = View.VISIBLE
        tvNoBudgetInSettings.visibility = View.GONE

        val remaining = budgetAmount - spent
        val percentageUsed = if (budgetAmount > 0) {
            ((spent / budgetAmount) * 100).toFloat().coerceAtMost(100f)
        } else {
            0f
        }

        tvBudgetStatus.text = when {
            percentageUsed >= 100 -> "Over Budget!"
            percentageUsed >= 80 -> "Nearing Budget Limit"
            else -> "On Track"
        }

        tvBudgetDetails.text = buildString {
            append("Budget: ${currencyFormat.format(budgetAmount)}\n")
            append("Spent: ${currencyFormat.format(spent)}\n")
            append("Remaining: ${currencyFormat.format(remaining)}")
        }

        budgetProgressBar.progress = percentageUsed.toInt()

        val color = when {
            percentageUsed >= 100 -> getColor(android.R.color.holo_red_dark)
            percentageUsed >= 80 -> getColor(android.R.color.holo_orange_dark)
            else -> getColor(android.R.color.holo_green_dark)
        }
        budgetProgressBar.setIndicatorColor(color)
        tvBudgetStatus.setTextColor(color)
    }

    private fun showNoBudget() {
        budgetProgressCard.visibility = View.GONE
        tvNoBudgetInSettings.visibility = View.VISIBLE
    }

    private fun setupClickListeners() {
        val categoryManagementLayout =
            findViewById<LinearLayout>(R.id.layoutManageCategories)
        val budgetManagementLayout =
            findViewById<LinearLayout>(R.id.layoutManageBudgets)

        categoryManagementLayout.setOnClickListener {
            startActivity(Intent(this, CategoryManagementActivity::class.java))
        }

        budgetManagementLayout.setOnClickListener {
            startActivity(Intent(this, BudgetManagementActivity::class.java))
        }

        budgetProgressCard.setOnClickListener {
            startActivity(Intent(this, BudgetManagementActivity::class.java))
        }
    }
}