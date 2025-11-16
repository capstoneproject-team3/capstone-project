package com.example.personalfinancetracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.R
import java.text.NumberFormat
import java.util.*

// Data class to hold category spending information
data class CategorySpending(
    val categoryName: String,
    val spent: Double
)

class CategoryAdapterWithBudget(
    private var categorySpendingList: List<CategorySpending>
) : RecyclerView.Adapter<CategoryAdapterWithBudget.CategoryViewHolder>() {

    private val currencyFormat = NumberFormat.getCurrencyInstance(
        Locale.Builder()
            .setLanguage("en")
            .setRegion("CA")
            .build()
    )

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_simple, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categorySpending = categorySpendingList[position]

        holder.tvCategory.text = categorySpending.categoryName
        holder.tvAmount.text = currencyFormat.format(categorySpending.spent)
    }

    override fun getItemCount() = categorySpendingList.size

    fun updateCategories(newCategories: List<CategorySpending>) {
        categorySpendingList = newCategories
        notifyDataSetChanged()
    }
}