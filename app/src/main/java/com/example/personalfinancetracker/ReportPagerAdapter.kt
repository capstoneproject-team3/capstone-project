package com.example.personalfinancetracker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.personalfinancetracker.ui.reports.MonthlyReportFragment
import com.example.personalfinancetracker.ui.reports.QuarterlyReportFragment
import com.example.personalfinancetracker.ui.reports.YearlyReportFragment

class ReportPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // Monthly, Quarterly, Yearly

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MonthlyReportFragment()
            1 -> QuarterlyReportFragment()
            2 -> YearlyReportFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}