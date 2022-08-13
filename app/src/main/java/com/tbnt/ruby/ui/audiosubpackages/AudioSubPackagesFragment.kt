package com.tbnt.ruby.ui.audiosubpackages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.AudioSubPackagesLayoutBinding
import com.tbnt.ruby.repo.AudioDataRepo

class AudioSubPackagesFragment : Fragment() {

    private val audioDataRepo = AudioDataRepo()
    private val subPackagesFragmentArgs: AudioSubPackagesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.audio_sub_packages_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = AudioSubPackagesLayoutBinding.bind(view)
        binding.subPackagesRv.addItemDecoration(
            DividerItemDecoration(
                view.context,
                RecyclerView.VERTICAL
            )
        )
        val adapter = AudioSubPackagesAdapter { title ->
            findNavController().navigate(
                AudioSubPackagesFragmentDirections.actionAudioSubPackagesFragmentToMediaPlayerFragment(
                    title
                )
            )
        }
        binding.subPackagesRv.adapter = adapter
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        audioDataRepo.fetchSubPackagesById().apply {
            adapter.submitList(this)

            binding.pageTitle.text = subPackagesFragmentArgs.pageTitle
        }
    }

}