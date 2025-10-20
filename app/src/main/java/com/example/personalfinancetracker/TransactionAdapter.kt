package com.example.personalfinancetracker.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.R
import com.example.personalfinancetracker.data.entity.Expense
import com.example.personalfinancetracker.data.entity.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val listener: OnTransactionClickListener
) : ListAdapter<Expense, TransactionAdapter.TransactionViewHolder>(ExpenseDiffCallback()) {

    interface OnTransactionClickListener {
        fun onEditClick(expense: Expense)
        fun onDeleteClick(expense: Expense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense, listener)
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTransactionName: TextView = itemView.findViewById(R.id.tvTransactionName)
        private val tvTransactionCategory: TextView = itemView.findViewById(R.id.tvTransactionCategory)
        private val tvTransactionDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        private val tvTransactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        private val ivRecurring: ImageView = itemView.findViewById(R.id.ivRecurring)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "CA"))
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(expense: Expense, listener: OnTransactionClickListener) {
            tvTransactionName.text = expense.expenseName
            tvTransactionCategory.text = expense.category
            tvTransactionDate.text = dateFormat.format(Date(expense.date))

            // Show recurring icon if applicable
            if (expense.isRecurring) {
                ivRecurring.visibility = View.VISIBLE
            } else {
                ivRecurring.visibility = View.GONE
            }

            // Set amount with proper formatting
            val amountText = if (expense.type == TransactionType.EXPENSE) {
                "-${currencyFormat.format(expense.amount)}"
            } else {
                "+${currencyFormat.format(expense.amount)}"
            }
            tvTransactionAmount.text = amountText

            // Set color based on transaction type
            if (expense.type == TransactionType.EXPENSE) {
                tvTransactionAmount.setTextColor(Color.parseColor("#D32F2F"))
            } else {
                tvTransactionAmount.setTextColor(Color.parseColor("#388E3C"))
            }

            // Set click listeners
            btnEdit.setOnClickListener {
                listener.onEditClick(expense)
            }

            btnDelete.setOnClickListener {
                listener.onDeleteClick(expense)
            }
        }
    }

    class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
}