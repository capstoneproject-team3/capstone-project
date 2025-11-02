package com.example.personalfinancetracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun setupBottomNavigation(bottomNav: BottomNavigationView, currentItemId: Int) {
        bottomNavigationView = bottomNav
        bottomNavigationView.selectedItemId = currentItemId

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (currentItemId != R.id.navigation_home) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navigation_transactions -> {
                    if (currentItemId != R.id.navigation_transactions) {
                        startActivity(Intent(this, TransactionsActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navigation_settings -> {
                    if (currentItemId != R.id.navigation_settings) {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}