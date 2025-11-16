package com.example.personalfinancetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.database.AppDatabase
import com.example.personalfinancetracker.data.entity.Category
import com.example.personalfinancetracker.data.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoryRepository
    val allCategories: LiveData<List<Category>>

    init {
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(categoryDao)
        allCategories = repository.allCategories
    }

    fun insert(category: Category) = viewModelScope.launch {
        repository.insert(category)
    }

    fun update(category: Category) = viewModelScope.launch {
        repository.update(category)
    }

    fun delete(category: Category) = viewModelScope.launch {
        repository.delete(category)
    }

    fun deleteNonDefaultCategory(id: Long) = viewModelScope.launch {
        repository.deleteNonDefaultCategory(id)
    }

    fun getCategoryById(id: Long): LiveData<Category?> {
        return repository.getCategoryById(id)
    }

    suspend fun getCategoryCountByName(name: String): Int {
        return repository.getCategoryCountByName(name)
    }
}