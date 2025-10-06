package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personalfinancetracker.data.entity.viewmodel.BillViewModel
import com.example.personalfinancetracker.databinding.ActivityBillListBinding
import com.example.personalfinancetracker.ui.home.BillAdapter

class BillListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBillListBinding
    // Note: You might need to adjust how you instantiate the ViewModel depending on your setup
    private val billViewModel: BillViewModel by viewModels()
    private lateinit var adapter: BillAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        adapter = BillAdapter(emptyList())
        binding.recyclerViewBills.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewBills.adapter = adapter

        // Observe LiveData from the ViewModel
        billViewModel.allBills.observe(this) { bills ->
            adapter.setData(bills)
        }

        // Navigate to Add Bill screen
        binding.btnAddBill.setOnClickListener {
            startActivity(Intent(this, AddBillActivity::class.java))
        }
    }
}