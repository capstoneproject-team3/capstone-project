package com.example.personalfinancetracker.data.entity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.database.AppDatabase
import com.example.personalfinancetracker.data.entity.Bill
import com.example.personalfinancetracker.data.entity.repository.BillRepository
import kotlinx.coroutines.launch

class BillViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BillRepository

    val allBills: LiveData<List<Bill>>

    init {
        val billDao = AppDatabase.getDatabase(application).billDao()
        repository = BillRepository(billDao)
        allBills = repository.allBills
    }

    fun insert(bill: Bill) = viewModelScope.launch {
        repository.insertBill(bill)
    }

    fun delete(bill: Bill) = viewModelScope.launch {
        repository.deleteBill(bill)
    }
}