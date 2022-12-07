package com.tbnt.ruby.ui.profile

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.LanguageMenuLayoutBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

const val LANGUAGE_CODE_KEY = "language_code_key"
const val LANGUAGE_CHANGED_KEY = "language_changed_key"

class SettingsBottomMenu : BottomSheetDialogFragment() {

    private val languageViewModel: LanguageViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.language_menu_layout, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = LanguageMenuLayoutBinding.bind(view)
        viewBinding.engLang.setOnClickListener {
            languageViewModel.languageChanged("ENG", view.context)
            dismiss()
        }
        viewBinding.rusLang.setOnClickListener {
            languageViewModel.languageChanged("RUS", view.context)
            dismiss()
        }
    }

    companion object {
        fun show(fragmentManager: FragmentManager) {
            val args = Bundle()
            val fragment = SettingsBottomMenu()
            fragment.arguments = args
            fragment.show(fragmentManager, "")
        }
    }
}