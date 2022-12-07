package com.tbnt.ruby.ui.myaudiobooks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tbnt.ruby.*
import com.tbnt.ruby.databinding.FragmentMyAudioBooksBinding
import com.tbnt.ruby.ui.profile.LanguageViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyAudioBooksFragment : Fragment() {
    private val myAudioBooksViewModel: MyAudioBooksViewModel by viewModel()
    private val languageViewModel: LanguageViewModel by sharedViewModel()
    private val dataViewModel: DataViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_audio_books, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMyAudioBooksBinding.bind(view)
        binding.emptyBtn.apply {
            setTextColor("#FFFFFF")
            setBgColor("#F5C037")
            setText(getString(R.string.gen_choose_guide))
            setOnClickListener {
                findNavController().navigate(R.id.audioBooksFragment)
            }
        }
        val myBooksAdapter = MyAudioBooksAdapter { title, id ->
            val action =
                MyAudioBooksFragmentDirections.actionMyAudioBooksFragmentToAudioSubPackagesFragment(
                    id, title
                )
            findNavController().navigate(action)
        }
        myAudioBooksViewModel.myAudioBooksFlow.onEach {
            binding.changeLanguageBtn.isVisible =
                !it.isNullOrEmpty() && languageViewModel.isLangChanged() && !myAudioBooksViewModel.isLangPackageExist(
                    chosenLanguage(binding.root.context)
                )
            binding.myAudioRv.updatePadding(bottom = (if(binding.changeLanguageBtn.isVisible) 60.toPx else 0).toInt())

            it?.let {
                myBooksAdapter.submitList(it) {
                    if (myBooksAdapter.currentList.isEmpty()) {
                        binding.emptyGroup.visibility = View.VISIBLE
                        binding.contentGroup.visibility = View.GONE
                    } else {
                        binding.emptyGroup.visibility = View.GONE
                        binding.contentGroup.visibility = View.VISIBLE
                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        dataViewModel.dataReadyFlow.onEach {
            if (it) myAudioBooksViewModel.loadMyAudioBooks()
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        myAudioBooksViewModel.loadMyAudioBooks()

        binding.myAudioRv.layoutManager = LinearLayoutManager(view.context)
        binding.myAudioRv.addItemDecoration(MyAudioBooksRvDecoration())
        binding.myAudioRv.adapter = myBooksAdapter

        binding.changeLanguageBtn.apply {
            setTextColor("#FFFFFF")
            setBgColor("#F5C037")
            val languageText = when (chosenLanguage(context)) {
                "ENG" -> context.getString(R.string.download_english_guides)
                else -> context.getString(R.string.download_russian_guides)
            }
            setText(languageText)
        }

        binding.changeLanguageBtn.setOnClickListener {
            if (activity?.isNetworkConnected() == true) {
                myBooksAdapter.currentList.forEach { audioPackage ->
                    myAudioBooksViewModel.downloadPackage(
                        audioPackage.id, chosenLanguage(it.context)
                    )
                }
                binding.changeLanguageBtn.isVisible = false
                binding.myAudioRv.updatePadding(bottom = 0)
                languageViewModel.deletedLangChanged()
                myAudioBooksViewModel.deletePreviousLangPackage(if (chosenLanguage(it.context) == "ENG") "RUS" else "ENG")
            }
        }
    }
}