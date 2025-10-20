package com.example.personalfinancetracker.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.personalfinancetracker.R
import com.example.personalfinancetracker.databinding.FragmentReportDetailBinding
import com.example.personalfinancetracker.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.util.*

class YearlyReportFragment : Fragment() {

    private var _binding: FragmentReportDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseViewModel by activityViewModels()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "CA"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🚩 CHANGE 1: Observe the YEARLY expense data
        viewModel.totalYearlyExpense.observe(viewLifecycleOwner) { expense ->
            val expenseValue = expense ?: 0.0
            binding.tvTotalExpense.text = currencyFormat.format(expenseValue)
            // Use yearly income value for net calculation
            updateNet(expenseValue, viewModel.totalYearlyIncome.value)
        }

        // 🚩 CHANGE 2: Observe the YEARLY income data
        viewModel.totalYearlyIncome.observe(viewLifecycleOwner) { income ->
            val incomeValue = income ?: 0.0
            binding.tvTotalIncome.text = currencyFormat.format(incomeValue)
            // Use yearly expense value for net calculation
            updateNet(viewModel.totalYearlyExpense.value, incomeValue)
        }
    }

    private fun updateNet(expense: Double?, income: Double?) {
        val totalExpense = expense ?: 0.0
        val totalIncome = income ?: 0.0
        val net = totalIncome - totalExpense

        binding.tvNetBalance.text = currencyFormat.format(net)

        val colorResId = if (net >= 0) android.R.color.holo_green_dark else android.R.color.holo_red_dark
        binding.tvNetBalance.setTextColor(
            ContextCompat.getColor(requireContext(), colorResId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}