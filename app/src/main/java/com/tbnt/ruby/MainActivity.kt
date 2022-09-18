package com.tbnt.ruby

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tbnt.ruby.databinding.MainLayoutBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : FragmentActivity() {

    private var binding: MainLayoutBinding? = null
    private val dataViewModel: DataViewModel by viewModel()
    private val keysField = FirebaseDatabase.getInstance().getReference("production")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        keysField.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataViewModel.storeData(snapshot)
                keysField.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                keysField.removeEventListener(this)
            }
        })

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

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}