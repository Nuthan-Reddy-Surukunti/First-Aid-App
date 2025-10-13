package com.example.firstaidapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * MainActivity - Main entry point of the First Aid Emergency Guide App
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Enable edge-to-edge display for modern Android UI
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)

            // Handle system window insets for proper edge-to-edge display
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            setupNavigation()

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: set a simple content view if navigation setup fails
            try {
                setContentView(android.R.layout.activity_list_item)
            } catch (fallbackException: Exception) {
                fallbackException.printStackTrace()
            }
        }
    }

    private fun setupNavigation() {
        try {
            // Set up Navigation Component with NavHostFragment
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            val navController = navHostFragment?.navController

            // Configure bottom navigation with navigation controller
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

            if (navController != null) {
                bottomNavigationView?.setupWithNavController(navController)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Navigation setup failed, but app can still run
        }
    }
}