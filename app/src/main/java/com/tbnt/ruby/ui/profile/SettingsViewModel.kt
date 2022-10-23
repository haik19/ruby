package com.tbnt.ruby.ui.profile

import androidx.lifecycle.ViewModel
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsViewModel(private val repo: RubyDataRepo) : ViewModel() {

    fun getContactUrl() = ""

    suspend fun getPolicyUrl() = withContext(Dispatchers.IO) {
        repo.getData()?.settingsInfo?.termsUrl
    }
}