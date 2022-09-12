package com.tbnt.ruby.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LanguageViewModel : ViewModel() {

    private val _languageDataLiveData = MutableLiveData<String>()
    val languageDataLiveData: LiveData<String> = _languageDataLiveData

    fun languageChanged(language: String) {
        _languageDataLiveData.value = language
    }
}