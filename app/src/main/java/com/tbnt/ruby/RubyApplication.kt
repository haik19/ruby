package com.tbnt.ruby

import android.app.Application
import com.google.firebase.FirebaseApp
import com.tbnt.ruby.di.appModule
import org.koin.core.context.startKoin

class RubyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule(applicationContext))
        }
        FirebaseApp.initializeApp(this)
    }
}