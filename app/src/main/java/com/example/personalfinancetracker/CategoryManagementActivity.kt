package com.example.personalfinancetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.data.entity.Category
import com.example.personalfinancetracker.ui.home.CategoryManageAdapter
import com.example.personalfinancetracker.viewmodel.CategoryViewModel
import com.google.android.material.button.MaterialButton

class CategoryManagementActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryManageAdapter
    private lateinit var rvCategories: RecyclerView
    private lateinit var btnAddCategory: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_management)

        // Set up the toolbar as action bar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        setupViews()
        observeData()
    }

    private fun setupViews() {
        rvCategories = findViewById(R.id.rvCategories)
        btnAddCategory = findViewById(R.id.fabAddCategory)

        categoryAdapter = CategoryManageAdapter(
            emptyList(),
            onEditClick = { category ->
                showEditCategoryDialog(category)
            },
            onDeleteClick = { category ->
                if (category.isDefault) {
                    Toast.makeText(this, R.string.cannot_delete_default, Toast.LENGTH_SHORT).show()
                } else {
                    showDeleteConfirmationDialog(category)
                }
            }
        )

        rvCategories.adapter = categoryAdapter
        rvCategories.layoutManager = LinearLayoutManager(this)

        btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.category_management)
    }

    private fun observeData() {
        viewModel.allCategories.observe(this) { categories ->
            categoryAdapter.updateCategories(categories)
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val etCategoryName = dialogView.findViewById<EditText>(R.id.etCategoryName)

        AlertDialog.Builder(this)
            .setTitle(R.string.add_category)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val categoryName = etCategoryName.text.toString().trim()
                if (categoryName.isNotEmpty()) {
                    val newCategory = Category(name = categoryName, isDefault = false)
                    viewModel.insert(newCategory)
                    Toast.makeText(this, R.string.category_added, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.category_empty, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showEditCategoryDialog(category: Category) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val etCategoryName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        etCategoryName.setText(category.name)

        AlertDialog.Builder(this)
            .setTitle(R.string.edit_category)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val newName = etCategoryName.text.toString().trim()
                if (newName.isNotEmpty()) {
                    val updatedCategory = category.copy(name = newName)
                    viewModel.update(updatedCategory)
                    Toast.makeText(this, R.string.category_updated, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.category_empty, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteConfirmationDialog(category: Category) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_category_title)
            .setMessage(R.string.delete_category_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.delete(category)
                Toast.makeText(this, R.string.category_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}