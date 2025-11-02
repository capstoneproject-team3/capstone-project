package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)

        setupBottomNavigation(
            findViewById(R.id.bottomNavigation),
            R.id.navigation_settings
        )

        setupClickListeners()
    }

    private fun setupClickListeners() {
        val categoryManagementLayout = findViewById<LinearLayout>(R.id.layoutManageCategories)

        categoryManagementLayout.setOnClickListener {
            val intent = Intent(this, CategoryManagementActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}