package com.example.personalfinancetracker.data.entity.repository

import com.example.personalfinancetracker.data.entity.dao.BillDao
import com.example.personalfinancetracker.data.entity.Bill
import androidx.lifecycle.LiveData

class BillRepository(private val billDao: BillDao) {

    val allBills: LiveData<List<Bill>> = billDao.getAllBills()

    suspend fun insertBill(bill: Bill) {
        billDao.insert(bill)
    }

    suspend fun deleteBill(bill: Bill) {
        billDao.delete(bill)
    }
}