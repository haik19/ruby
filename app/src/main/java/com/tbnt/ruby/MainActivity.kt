package com.tbnt.ruby

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tbnt.ruby.databinding.MainLayoutBinding

class MainActivity : FragmentActivity() {

    private var binding: MainLayoutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val s = findViewById<BottomNavigationView>(R.id.navigation_view)
        s.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, args ->
            when (destination.id) {
                R.id.audioPreviewFragment -> if (args?.getBoolean("showPlayBtn") == true) showBottomNav() else hideBottomNav()
                R.id.feedBackFragment,
                R.id.mediaPlayerFragment -> hideBottomNav()
                else -> showBottomNav()
            }
        }
    }

    private fun showBottomNav() = binding?.run {
        navigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() = binding?.run {
        navigationView.visibility = View.GONE
    }
}