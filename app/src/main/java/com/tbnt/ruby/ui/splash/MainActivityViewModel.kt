package com.tbnt.ruby.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashViewModel(private val repo: RubyDataRepo) : ViewModel() {
    fun storeData(snapshot: DataSnapshot) {
        viewModelScope.launch(Dispatchers.Default) {
            repo.storeData(snapshot)
        }
    }
}