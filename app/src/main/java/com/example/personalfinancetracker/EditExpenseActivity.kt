package com.example.personalfinancetracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.example.personalfinancetracker.data.entity.Expense
import com.example.personalfinancetracker.data.entity.RecurrencePeriod
import com.example.personalfinancetracker.data.entity.TransactionType
import com.example.personalfinancetracker.viewmodel.ExpenseViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var etExpenseName: TextInputEditText
    private lateinit var etAmount: TextInputEditText
    private lateinit var etDate: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerRecurrence: Spinner
    private lateinit var rgTransactionType: RadioGroup
    private lateinit var rbExpense: RadioButton
    private lateinit var rbIncome: RadioButton
    private lateinit var cbIsRecurring: CheckBox
    private lateinit var tvRecurrenceLabel: TextView
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private var selectedDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var expenseId: Int = 0

    private val expenseCategories = arrayOf(
        "Food", "Transport", "Bills", "Shopping",
        "Entertainment", "Healthcare", "Education", "Other"
    )

    private val incomeCategories = arrayOf(
        "Salary", "Freelance", "Investment", "Gift", "Other"
    )

    private val recurrencePeriods = arrayOf(
        "Daily", "Weekly", "Monthly", "Yearly"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        setupStatusBar()
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        initViews()
        setupCategorySpinner()
        setupRecurrenceSpinner()
        setupDatePicker()
        setupRecurringCheckbox()
        setupTransactionTypeToggle()
        setupSaveButton()
        setupCancelButton()

        supportActionBar?.title = "Edit Transaction"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load existing expense data
        loadExpenseData()
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

    private fun initViews() {
        etExpenseName = findViewById(R.id.etExpenseName)
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerRecurrence = findViewById(R.id.spinnerRecurrence)
        rgTransactionType = findViewById(R.id.rgTransactionType)
        rbExpense = findViewById(R.id.rbExpense)
        rbIncome = findViewById(R.id.rbIncome)
        cbIsRecurring = findViewById(R.id.cbIsRecurring)
        tvRecurrenceLabel = findViewById(R.id.tvRecurrenceLabel)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        btnSave.text = "Update"
    }

    private fun loadExpenseData() {
        expenseId = intent.getIntExtra("expense_id", 0)
        val name = intent.getStringExtra("expense_name") ?: ""
        val amount = intent.getDoubleExtra("expense_amount", 0.0)
        val category = intent.getStringExtra("expense_category") ?: ""
        val date = intent.getLongExtra("expense_date", System.currentTimeMillis())
        val description = intent.getStringExtra("expense_description") ?: ""
        val type = intent.getStringExtra("expense_type") ?: "EXPENSE"
        val isRecurring = intent.getBooleanExtra("expense_recurring", false)
        val recurrencePeriod = intent.getStringExtra("expense_recurrence_period")

        // Set values
        etExpenseName.setText(name)
        etAmount.setText(amount.toString())
        etDescription.setText(description)

        selectedDate.timeInMillis = date
        etDate.setText(dateFormat.format(selectedDate.time))

        // Set transaction type
        if (type == "EXPENSE") {
            rbExpense.isChecked = true
            setupCategorySpinner()
        } else {
            rbIncome.isChecked = true
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, incomeCategories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        // Set category
        val categoryArray = if (type == "EXPENSE") expenseCategories else incomeCategories
        val categoryPosition = categoryArray.indexOf(category)
        if (categoryPosition >= 0) {
            spinnerCategory.setSelection(categoryPosition)
        }

        // Set recurring
        cbIsRecurring.isChecked = isRecurring
        if (isRecurring) {
            spinnerRecurrence.visibility = View.VISIBLE
            tvRecurrenceLabel.visibility = View.VISIBLE

            recurrencePeriod?.let {
                val periodPosition = when(it) {
                    "DAILY" -> 0
                    "WEEKLY" -> 1
                    "MONTHLY" -> 2
                    "YEARLY" -> 3
                    else -> 0
                }
                spinnerRecurrence.setSelection(periodPosition)
            }
        }
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, expenseCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun setupRecurrenceSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, recurrencePeriods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRecurrence.adapter = adapter
    }

    private fun setupDatePicker() {
        etDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate.set(year, month, dayOfMonth)
                    etDate.setText(dateFormat.format(selectedDate.time))
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun setupRecurringCheckbox() {
        cbIsRecurring.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                spinnerRecurrence.visibility = View.VISIBLE
                tvRecurrenceLabel.visibility = View.VISIBLE
            } else {
                spinnerRecurrence.visibility = View.GONE
                tvRecurrenceLabel.visibility = View.GONE
            }
        }
    }

    private fun setupTransactionTypeToggle() {
        rgTransactionType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbExpense -> {
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, expenseCategories)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter
                }
                R.id.rbIncome -> {
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, incomeCategories)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter
                }
            }
        }
    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            updateExpense()
        }
    }

    private fun setupCancelButton() {
        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun updateExpense() {
        val name = etExpenseName.text.toString().trim()
        val amountStr = etAmount.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val description = etDescription.text.toString().trim()
        val isRecurring = cbIsRecurring.isChecked

        // Validation
        if (name.isEmpty()) {
            etExpenseName.error = "Please enter a name"
            return
        }

        if (amountStr.isEmpty()) {
            etAmount.error = "Please enter an amount"
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            etAmount.error = "Please enter a valid amount"
            return
        }

        // Determine transaction type
        val type = if (rbExpense.isChecked) TransactionType.EXPENSE else TransactionType.INCOME

        // Get recurrence period if recurring
        val recurrencePeriod = if (isRecurring) {
            when (spinnerRecurrence.selectedItem.toString()) {
                "Daily" -> RecurrencePeriod.DAILY
                "Weekly" -> RecurrencePeriod.WEEKLY
                "Monthly" -> RecurrencePeriod.MONTHLY
                "Yearly" -> RecurrencePeriod.YEARLY
                else -> null
            }
        } else {
            null
        }

        // Calculate next due date if recurring
        val nextDueDate = if (isRecurring && recurrencePeriod != null) {
            calculateNextDueDate(selectedDate.timeInMillis, recurrencePeriod)
        } else {
            null
        }

        // Create updated expense object with existing ID
        val updatedExpense = Expense(
            id = expenseId.toLong(),
            expenseName = name,
            amount = amount,
            category = category,
            date = selectedDate.timeInMillis,
            description = description,
            type = type,
            isRecurring = isRecurring,
            recurrencePeriod = recurrencePeriod,
            nextDueDate = nextDueDate
        )

        // Update in database
        viewModel.update(updatedExpense)

        // Show success message
        Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show()

        // Close activity
        finish()
    }

    private fun calculateNextDueDate(currentDate: Long, period: RecurrencePeriod): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentDate

        when (period) {
            RecurrencePeriod.DAILY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
            RecurrencePeriod.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            RecurrencePeriod.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            RecurrencePeriod.YEARLY -> calendar.add(Calendar.YEAR, 1)
        }

        return calendar.timeInMillis
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}