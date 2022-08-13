package com.tbnt.ruby.ui.myaudiobooks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.FragmentMyAudioBooksBinding
import com.tbnt.ruby.repo.AudioDataRepo

class MyAudioBooksFragment : Fragment() {
    private val dataRepo = AudioDataRepo()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_audio_books, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMyAudioBooksBinding.bind(view)
        val myBooksAdapter = MyAudioBooksAdapter { title ->
            val action =
                MyAudioBooksFragmentDirections.actionMyAudioBooksFragmentToAudioSubPackagesFragment(
                    title
                )
            findNavController().navigate(action)
        }
        myBooksAdapter.submitList(dataRepo.fetchMyBooksData())
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