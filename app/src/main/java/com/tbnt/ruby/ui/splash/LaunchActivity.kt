package com.tbnt.ruby.ui.splash

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.tbnt.ruby.MainActivity
import com.tbnt.ruby.R
import com.tbnt.ruby.isNetworkConnected
import com.tbnt.ruby.payment.GooglePaymentService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LaunchActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)
        if (!isNetworkConnected()) {
            MainActivity.startActivity(this@LaunchActivity)
            finish()
            return
        }
        GooglePaymentService.init(applicationContext) {
            lifecycleScope.launch {
                delay(1000)
                MainActivity.startActivity(this@LaunchActivity)
                finish()
            }
        }
    }
}