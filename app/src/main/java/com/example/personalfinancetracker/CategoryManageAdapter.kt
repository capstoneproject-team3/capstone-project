package com.example.personalfinancetracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.R
import com.example.personalfinancetracker.data.entity.Category

class CategoryManageAdapter(
    private var categories: List<Category>,
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryManageAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvDefaultBadge: TextView = itemView.findViewById(R.id.tvDefaultBadge)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEditCategory)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_manage, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        holder.tvCategoryName.text = category.name

        if (category.isDefault) {
            holder.tvDefaultBadge.visibility = View.VISIBLE
            holder.btnDelete.isEnabled = false
            holder.btnDelete.alpha = 0.3f
        } else {
            holder.tvDefaultBadge.visibility = View.GONE
            holder.btnDelete.isEnabled = true
            holder.btnDelete.alpha = 1.0f
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(category)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(category)
        }
    }

    override fun getItemCount() = categories.size

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}