package com.example.personalfinancetracker.data.repository

import androidx.lifecycle.LiveData
import com.example.personalfinancetracker.data.dao.CategoryDao
import com.example.personalfinancetracker.data.entity.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun update(category: Category) {
        categoryDao.update(category)
    }

    suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }

    suspend fun deleteNonDefaultCategory(id: Long) {
        categoryDao.deleteNonDefaultCategory(id)
    }

    fun getCategoryById(id: Long): LiveData<Category?> {
        return categoryDao.getCategoryById(id)
    }

    suspend fun getCategoryCountByName(name: String): Int {
        return categoryDao.getCategoryCountByName(name)
    }
}