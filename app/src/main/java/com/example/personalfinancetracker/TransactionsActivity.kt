package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.ui.home.ExpenseAdapter
import com.example.personalfinancetracker.viewmodel.ExpenseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class TransactionsActivity : BaseActivity() {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var rvAllTransactions: RecyclerView
    private lateinit var tvNoTransactions: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_transactions)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.all_transactions)

        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        setupViews()
        setupBottomNavigation(
            findViewById(R.id.bottomNavigation),
            R.id.navigation_transactions
        )
        observeData()
    }

    private fun setupViews() {
        rvAllTransactions = findViewById(R.id.rvAllTransactions)
        tvNoTransactions = findViewById(R.id.tvNoTransactions)

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

        rvAllTransactions.adapter = expenseAdapter
        rvAllTransactions.layoutManager = LinearLayoutManager(this)
    }

    private fun observeData() {
        viewModel.allExpenses.observe(this) { expenses ->
            if (expenses.isEmpty()) {
                rvAllTransactions.visibility = View.GONE
                tvNoTransactions.visibility = View.VISIBLE
            } else {
                rvAllTransactions.visibility = View.VISIBLE
                tvNoTransactions.visibility = View.GONE
                expenseAdapter.updateExpenses(expenses)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}