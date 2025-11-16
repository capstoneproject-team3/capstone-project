package com.example.personalfinancetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.personalfinancetracker.data.entity.Budget
import com.example.personalfinancetracker.data.entity.TransactionType
import com.example.personalfinancetracker.viewmodel.BudgetViewModel
import com.example.personalfinancetracker.viewmodel.ExpenseViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.NumberFormat
import java.util.*

class BudgetManagementActivity : AppCompatActivity() {

    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var fabAddBudget: MaterialButton

    private lateinit var tvBudgetAmount: TextView
    private lateinit var tvSpentAmount: TextView
    private lateinit var tvRemainingAmount: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var tvNoBudget: TextView
    private lateinit var budgetContainer: LinearLayout
    private lateinit var tvMonthYear: TextView
    private lateinit var btnEditBudget: ImageButton
    private lateinit var btnDeleteBudget: ImageButton

    private val currencyFormat = NumberFormat.getCurrencyInstance()
    private var currentBudget: Budget? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_management)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        budgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        setupViews()
        observeData()
    }

    private fun setupViews() {
        fabAddBudget = findViewById(R.id.fabAddBudget)
        tvBudgetAmount = findViewById(R.id.tvBudgetAmount)
        tvSpentAmount = findViewById(R.id.tvSpentAmount)
        tvRemainingAmount = findViewById(R.id.tvRemainingAmount)
        progressBar = findViewById(R.id.progressBar)
        tvNoBudget = findViewById(R.id.tvNoBudget)
        budgetContainer = findViewById(R.id.budgetContainer)
        tvMonthYear = findViewById(R.id.tvMonthYear)
        btnEditBudget = findViewById(R.id.btnEditBudget)
        btnDeleteBudget = findViewById(R.id.btnDeleteBudget)

        fabAddBudget.setOnClickListener {
            if (currentBudget == null) {
                showBudgetDialog(null)
            } else {
                Toast.makeText(this, "Budget already exists for this month. Use edit to modify.", Toast.LENGTH_SHORT).show()
            }
        }

        btnEditBudget.setOnClickListener {
            currentBudget?.let { showBudgetDialog(it) }
        }

        btnDeleteBudget.setOnClickListener {
            currentBudget?.let { showDeleteConfirmation(it) }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Budget Management"
    }

    private fun observeData() {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        // Set month/year display
        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        tvMonthYear.text = "$monthName $currentYear"

        // Observe budget for current month
        budgetViewModel.getBudgetsForMonth(currentMonth, currentYear).observe(this) { budgets ->
            if (budgets.isNotEmpty()) {
                currentBudget = budgets[0]

                // Get expenses for current month
                expenseViewModel.getCurrentMonthExpenses().observe(this) { expenses ->
                    val totalSpent = expenseViewModel.calculateTotal(expenses, TransactionType.EXPENSE)
                    updateBudgetDisplay(budgets[0], totalSpent)
                }
            } else {
                currentBudget = null
                showNoBudgetMessage()
            }
        }
    }

    private fun updateBudgetDisplay(budget: Budget, spent: Double) {
        tvNoBudget.visibility = android.view.View.GONE
        budgetContainer.visibility = android.view.View.VISIBLE
        fabAddBudget.visibility = android.view.View.GONE

        val remaining = budget.amount - spent
        val percentageUsed = if (budget.amount > 0) {
            ((spent / budget.amount) * 100).toFloat().coerceAtMost(100f)
        } else {
            0f
        }

        tvBudgetAmount.text = "Budget: ${currencyFormat.format(budget.amount)}"
        tvSpentAmount.text = "Spent: ${currencyFormat.format(spent)}"
        tvRemainingAmount.text = "Remaining: ${currencyFormat.format(remaining)}"

        progressBar.progress = percentageUsed.toInt()

        // Change color based on budget status
        when {
            percentageUsed >= 100 -> {
                progressBar.setIndicatorColor(getColor(android.R.color.holo_red_dark))
                tvRemainingAmount.setTextColor(getColor(android.R.color.holo_red_dark))
            }
            percentageUsed >= 80 -> {
                progressBar.setIndicatorColor(getColor(android.R.color.holo_orange_dark))
                tvRemainingAmount.setTextColor(getColor(android.R.color.holo_orange_dark))
            }
            else -> {
                progressBar.setIndicatorColor(getColor(android.R.color.holo_green_dark))
                tvRemainingAmount.setTextColor(getColor(android.R.color.holo_green_dark))
            }
        }
    }

    private fun showNoBudgetMessage() {
        tvNoBudget.visibility = android.view.View.VISIBLE
        budgetContainer.visibility = android.view.View.GONE
        fabAddBudget.visibility = android.view.View.VISIBLE
    }

    private fun showBudgetDialog(existingBudget: Budget?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_overall_budget, null)
        val etBudgetAmount = dialogView.findViewById<EditText>(R.id.etBudgetAmount)
        val spinnerMonth = dialogView.findViewById<Spinner>(R.id.spinnerMonth)
        val spinnerYear = dialogView.findViewById<Spinner>(R.id.spinnerYear)

        // Setup month spinner
        val months = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)
        spinnerMonth.adapter = monthAdapter

        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        spinnerMonth.setSelection(existingBudget?.month?.minus(1) ?: currentMonth)

        // Setup year spinner
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear..currentYear + 5).map { it.toString() }
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)
        spinnerYear.adapter = yearAdapter

        val yearIndex = years.indexOf((existingBudget?.year ?: currentYear).toString())
        if (yearIndex >= 0) spinnerYear.setSelection(yearIndex)

        // Pre-fill amount if editing
        existingBudget?.let {
            etBudgetAmount.setText(it.amount.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(if (existingBudget == null) "Set Overall Budget" else "Edit Budget")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val amountText = etBudgetAmount.text.toString().trim()
                val month = spinnerMonth.selectedItemPosition + 1
                val year = spinnerYear.selectedItem.toString().toInt()

                if (amountText.isNotEmpty()) {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        if (existingBudget != null) {
                            val updatedBudget = existingBudget.copy(
                                amount = amount,
                                month = month,
                                year = year
                            )
                            budgetViewModel.update(updatedBudget)
                            Toast.makeText(this, "Budget updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            val newBudget = Budget(
                                amount = amount,
                                month = month,
                                year = year
                            )
                            budgetViewModel.insert(newBudget)
                            Toast.makeText(this, "Budget set successfully", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(budget: Budget) {
        AlertDialog.Builder(this)
            .setTitle("Delete Budget")
            .setMessage("Are you sure you want to delete this budget?")
            .setPositiveButton("Delete") { _, _ ->
                budgetViewModel.delete(budget)
                Toast.makeText(this, "Budget deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}