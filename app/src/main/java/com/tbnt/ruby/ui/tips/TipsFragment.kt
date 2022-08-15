package com.tbnt.ruby.ui.tips

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.FragmentTipsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class TipsFragment : Fragment() {

    private val tipsPageViewModel: TipsPageViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTipsBinding.bind(view)
        val tipsAdapter = TipsAdapter()

        tipsPageViewModel.tipsDataFlow.onEach {
            it?.let {
                tipsAdapter.submitList(it)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        tipsPageViewModel.loadTipsData()

        binding.tipsRv.addItemDecoration(TipsRvDecoration())
        binding.tipsRv.adapter = tipsAdapter
    }
}