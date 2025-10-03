package com.example.personalfinancetracker.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.R
import java.text.NumberFormat
import java.util.*

class CategoryAdapter(
    private var categories: Map<String, Double>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private var categoryList = categories.toList().sortedByDescending { it.second }.take(5)

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvCategoryAmount: TextView = itemView.findViewById(R.id.tvCategoryAmount)
        val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val (category, amount) = categoryList[position]

        holder.tvCategoryName.text = category
        holder.tvCategoryAmount.text = currencyFormat.format(amount)

        // Set category icon as first letter with colored background
        val categoryInitial = category.firstOrNull()?.uppercase() ?: "?"
        holder.tvCategoryIcon.text = categoryInitial
        holder.tvCategoryIcon.setBackgroundColor(getCategoryColor(category))
    }

    override fun getItemCount() = categoryList.size

    fun updateCategories(newCategories: Map<String, Double>) {
        categories = newCategories
        categoryList = categories.toList().sortedByDescending { it.second }.take(5)
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
            else -> Color.parseColor("#95A5A6")
        }
    }
}