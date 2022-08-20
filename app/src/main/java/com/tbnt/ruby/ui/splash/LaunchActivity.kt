package com.tbnt.ruby.ui.splash

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tbnt.ruby.MainActivity
import com.tbnt.ruby.R
import com.tbnt.ruby.isNetworkConnected
import org.koin.androidx.viewmodel.ext.android.viewModel

class LaunchActivity : FragmentActivity() {
    private val splashViewModel: SplashViewModel by viewModel()
    private val keysField = FirebaseDatabase.getInstance().getReference("production")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)
        if (!isNetworkConnected()) {
            MainActivity.startActivity(this@LaunchActivity)
            finish()
            return
        }
        keysField.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                splashViewModel.storeData(snapshot)
                MainActivity.startActivity(this@LaunchActivity)
                keysField.removeEventListener(this)
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                MainActivity.startActivity(this@LaunchActivity)
                keysField.removeEventListener(this)
                finish()
            }
        })
    }
}