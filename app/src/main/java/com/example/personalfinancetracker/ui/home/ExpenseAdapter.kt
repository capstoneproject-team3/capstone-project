package com.example.personalfinancetracker.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private val onItemClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExpenseName: TextView = itemView.findViewById(R.id.tvExpenseName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        holder.tvExpenseName.text = expense.expenseName
        holder.tvCategory.text = expense.category
        holder.tvDate.text = dateFormat.format(Date(expense.date))

        val amountText = if (expense.type == TransactionType.EXPENSE) {
            "-${currencyFormat.format(expense.amount)}"
        } else {
            "+${currencyFormat.format(expense.amount)}"
        }
        holder.tvAmount.text = amountText

        val amountColor = if (expense.type == TransactionType.EXPENSE) {
            android.R.color.holo_red_dark
        } else {
            android.R.color.holo_green_dark
        }
        holder.tvAmount.setTextColor(holder.itemView.context.getColor(amountColor))

        // Set category icon as first letter with colored background
        val categoryInitial = expense.category.firstOrNull()?.uppercase() ?: "?"
        holder.tvCategoryIcon.text = categoryInitial
        holder.tvCategoryIcon.setBackgroundColor(getCategoryColor(expense.category))

        holder.itemView.setOnClickListener {
            onItemClick(expense)
        }
    }

    override fun getItemCount() = expenses.size

    fun updateExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    private fun getCategoryColor(category: String): Int {
        return when (category.lowercase()) {
            "food" -> Color.parseColor("#FF6B6B")
            "transport" -> Color.parseColor("#4ECDC4")
            "bills" -> Color.parseColor("#45B7D1")
            "shopping" -> Color.parseColor("#FFA07A")
            "entertainment" -> Color.parseColor("#98D8C8")
            "healthcare" -> Color.parseColor("#F7B731")
            "education" -> Color.parseColor("#5F27CD")
            "salary" -> Color.parseColor("#00D2D3")
            "freelance" -> Color.parseColor("#FF9FF3")
            else -> Color.parseColor("#95A5A6")
        }
    }
}