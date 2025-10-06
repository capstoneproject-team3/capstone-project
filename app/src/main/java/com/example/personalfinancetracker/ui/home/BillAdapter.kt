package com.example.personalfinancetracker.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.R
import com.example.personalfinancetracker.data.entity.Bill
import java.text.SimpleDateFormat
import java.util.*

class BillAdapter(private var bills: List<Bill>) :
    RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    inner class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBillName: TextView = itemView.findViewById(R.id.tvBillName)
        val tvBillAmount: TextView = itemView.findViewById(R.id.tvBillAmount)
        val tvBillDueDate: TextView = itemView.findViewById(R.id.tvBillDueDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        holder.tvBillName.text = bill.name
        holder.tvBillAmount.text = "$${String.format("%.2f", bill.amount)}" // Format to 2 decimal places

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.tvBillDueDate.text = "Due: ${formatter.format(Date(bill.dueDate))}"
    }

    override fun getItemCount(): Int = bills.size

    fun setData(newBills: List<Bill>) {
        bills = newBills
        notifyDataSetChanged()
    }
}