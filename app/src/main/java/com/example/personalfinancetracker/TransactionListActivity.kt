package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.adapter.TransactionAdapter
import com.example.personalfinancetracker.data.entity.Expense
import com.example.personalfinancetracker.viewmodel.ExpenseViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TransactionListActivity : AppCompatActivity(), TransactionAdapter.OnTransactionClickListener {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var tvNoTransactions: TextView
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        // Setup action bar
        supportActionBar?.title = "All Transactions"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views
        recyclerView = findViewById(R.id.rvTransactions)
        tvNoTransactions = findViewById(R.id.tvNoTransactions)
        fabAdd = findViewById(R.id.fabAddTransaction)

        // Setup RecyclerView
        adapter = TransactionAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Observe expenses
        viewModel.allExpenses.observe(this) { expenses ->
            if (expenses.isEmpty()) {
                recyclerView.visibility = View.GONE
                tvNoTransactions.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                tvNoTransactions.visibility = View.GONE
                adapter.submitList(expenses)
            }
        }

        // Setup FAB
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onEditClick(expense: Expense) {
        // Launch EditExpenseActivity with expense data
        val intent = Intent(this, EditExpenseActivity::class.java).apply {
            putExtra("expense_id", expense.id.toInt())  // Convert Long to Int here
            putExtra("expense_name", expense.expenseName)
            putExtra("expense_amount", expense.amount)
            putExtra("expense_category", expense.category)
            putExtra("expense_date", expense.date)
            putExtra("expense_description", expense.description ?: "")
            putExtra("expense_type", expense.type.name)
            putExtra("expense_recurring", expense.isRecurring)
            putExtra("expense_recurrence_period", expense.recurrencePeriod?.name)
        }
        startActivity(intent)
    }

    override fun onDeleteClick(expense: Expense) {
        // Show confirmation dialog
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete '${expense.expenseName}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.delete(expense)
                Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}