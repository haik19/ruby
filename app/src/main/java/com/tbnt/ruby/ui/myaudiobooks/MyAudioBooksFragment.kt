package com.tbnt.ruby.ui.myaudiobooks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.FragmentMyAudioBooksBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyAudioBooksFragment : Fragment() {
    private val myAudioBooksViewModel: MyAudioBooksViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_audio_books, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMyAudioBooksBinding.bind(view)
        val myBooksAdapter = MyAudioBooksAdapter { title, id ->
            val action =
                MyAudioBooksFragmentDirections.actionMyAudioBooksFragmentToAudioSubPackagesFragment(
                    id,
                    title
                )
            findNavController().navigate(action)
        }
        myAudioBooksViewModel.myAudioBooksFlow.onEach {
            it?.let {
                myBooksAdapter.submitList(it)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        myAudioBooksViewModel.loadMyAudioBooks()

        binding.myAudioRv.layoutManager = LinearLayoutManager(view.context)
        binding.myAudioRv.addItemDecoration(MyAudioBooksRvDecoration())
        binding.myAudioRv.adapter = myBooksAdapter

        binding.audioBtn.apply {
            setTextColor("#FFFFFF")
            setBgColor("#F5C037")
            setText(getString(R.string.gen_language_ribbon_text))
        }
    }
}