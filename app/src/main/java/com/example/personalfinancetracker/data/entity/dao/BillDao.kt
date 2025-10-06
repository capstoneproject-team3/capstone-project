package com.example.personalfinancetracker.data.entity.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.personalfinancetracker.data.entity.Bill // <--- Ensure this path is correct

@Dao
interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: Bill)

    @Update
    suspend fun update(bill: Bill)

    @Delete
    suspend fun delete(bill: Bill)

    @Query("SELECT * FROM bills ORDER BY dueDate ASC")
    fun getAllBills(): LiveData<List<Bill>> // <-- This line is what the error points to
}