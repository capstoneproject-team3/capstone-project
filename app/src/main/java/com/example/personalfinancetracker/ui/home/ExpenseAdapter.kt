package com.example.personalfinancetracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.R
import com.example.personalfinancetracker.data.entity.Expense
import com.example.personalfinancetracker.data.entity.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onEditClick: (Expense) -> Unit,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(
        Locale.Builder()
            .setLanguage("en")
            .setRegion("CA")
            .build()
    )

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTransactionName: TextView = itemView.findViewById(R.id.tvTransactionName)
        val tvTransactionCategory: TextView = itemView.findViewById(R.id.tvTransactionCategory)
        val tvTransactionDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        val tvTransactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        holder.tvTransactionName.text = expense.expenseName
        holder.tvTransactionCategory.text = expense.category
        holder.tvTransactionDate.text = dateFormat.format(Date(expense.date))

        val amountText = if (expense.type == TransactionType.EXPENSE) {
            "-${currencyFormat.format(expense.amount)}"
        } else {
            "+${currencyFormat.format(expense.amount)}"
        }
        holder.tvTransactionAmount.text = amountText

        val amountColor = if (expense.type == TransactionType.EXPENSE) {
            android.R.color.holo_red_dark
        } else {
            android.R.color.holo_green_dark
        }
        holder.tvTransactionAmount.setTextColor(holder.itemView.context.getColor(amountColor))

        // Handle Edit button click
        holder.btnEdit.setOnClickListener {
            onEditClick(expense)
        }

        // Handle Delete button click
        holder.btnDelete.setOnClickListener {
            onDeleteClick(expense)
        }
    }

    override fun getItemCount() = expenses.size

    fun updateExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}