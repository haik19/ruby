package com.tbnt.ruby.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val PLAY_STORE_LINK =
    "https://play.google.com/store/apps/details?id=am.ggtaxi.main"

private const val PLAY_STORE_PACKAGE =
    "com.android.vending"

private const val HELP_SUPPORT = "https://play.google.com/store/games?hl=ru&gl=US"

class SettingsFragment : Fragment() {

    private val languageViewModel: LanguageViewModel by sharedViewModel()
    private val settingsViewModel: SettingsViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view)
        binding.contactUs.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(settingsViewModel.getContactEmail()))
                    type = "message/rfc822";
                }
                startActivity(intent)
            }
        }
        binding.rate.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(PLAY_STORE_LINK)
                setPackage(PLAY_STORE_PACKAGE) //TODO CHANGE
            }
            startActivity(intent)
        }
        binding.terms.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val url = settingsViewModel.getPolicyUrl()
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url.orEmpty())
                }
                startActivity(intent)
            }

        }
        binding.tellAFriend.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "our app");//TODO share
            sharingIntent.type = "text/plain"
            startActivity(Intent.createChooser(sharingIntent, "Share"))
        }

        binding.guideLanguage.setOnClickListener {
            SettingsBottomMenu.show(parentFragmentManager)
        }

        languageViewModel.languageDataLiveData.observe(viewLifecycleOwner) {
            binding.guidLanguageCode.text = it
        }
    }
}