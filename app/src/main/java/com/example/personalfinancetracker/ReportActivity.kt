package com.example.personalfinancetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.personalfinancetracker.databinding.ActivityReportBinding
import com.google.android.material.tabs.TabLayoutMediator

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding

    // The titles for the tabs
    private val tabTitles = arrayOf("Monthly", "Quarterly", "Yearly")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup the Toolbar
        // This line is essential: it tells the system to use the MaterialToolbar for ActionBar functionality.
        setSupportActionBar(binding.toolbar)

        // This displays the back arrow (Up button).
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Financial Reports"

        // Setup the ViewPager Adapter
        val adapter = ReportPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Link the TabLayout and ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    // This method handles the click event of the back arrow (Up button)
    override fun onSupportNavigateUp(): Boolean {
        // Use the modern dispatcher for reliable back navigation
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}