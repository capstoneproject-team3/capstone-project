package com.example.personalfinancetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.personalfinancetracker.data.entity.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Long): LiveData<Category?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT COUNT(*) FROM categories WHERE name = :name")
    suspend fun getCategoryCountByName(name: String): Int

    @Query("DELETE FROM categories WHERE id = :id AND isDefault = 0")
    suspend fun deleteNonDefaultCategory(id: Long)
}