package com.tbnt.ruby.ui.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tbnt.ruby.PreferencesService
import com.tbnt.ruby.R
import com.tbnt.ruby.chosenLanguage

class LanguageViewModel(private val preferencesService: PreferencesService) : ViewModel() {

    private val _languageDataLiveData = MutableLiveData<String>()
    val languageDataLiveData: LiveData<String> = _languageDataLiveData

    fun languageChanged(langCode: String, context: Context) {
        val isLangChanged = chosenLanguage(context) != langCode
        preferencesService.putPreferences(LANGUAGE_CODE_KEY, langCode)
        preferencesService.putPreferences(LANGUAGE_CHANGED_KEY, isLangChanged)
        _languageDataLiveData.postValue(
            when (chosenLanguage(context)) {
                "ENG" -> context.getString(R.string.gen_eng)
                else -> context.getString(R.string.gen_rus)
            }
        )
    }

    fun isLangChanged() = preferencesService.preference(LANGUAGE_CHANGED_KEY, false)

    fun deletedLangChanged() = preferencesService.preference(LANGUAGE_CHANGED_KEY, false)
}