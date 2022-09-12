package com.tbnt.ruby.ui.audiosubpackages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.AudioSubPackagesLayoutBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioSubPackagesFragment : Fragment() {

    private val subPackagesFragmentArgs: AudioSubPackagesFragmentArgs by navArgs()
    private val subPackagesViewModel: AudioSubPackagesViewModel by viewModel()

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
        val adapter = AudioSubPackagesAdapter { id, index ->
            findNavController().navigate(
                AudioSubPackagesFragmentDirections.actionAudioSubPackagesFragmentToMediaPlayerFragment(
                    id, index, false
                )
            )
        }
        binding.subPackagesRv.adapter = adapter
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        subPackagesViewModel.packagesDataFlow.onEach {
            it?.let {
                adapter.submitList(it)
                binding.pageTitle.text = subPackagesFragmentArgs.pageTitle
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        subPackagesViewModel.loadSubPackages(subPackagesFragmentArgs.audioId)
    }
}