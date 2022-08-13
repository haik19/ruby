package com.tbnt.ruby.ui.tips

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.FragmentTipsBinding
import com.tbnt.ruby.repo.AudioDataRepo


class TipsFragment : Fragment() {
    private val dataRepo = AudioDataRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        tipsAdapter.submitList(dataRepo.fetchTipsData())
        binding.tipsRv.addItemDecoration(TipsRvDecoration())
        binding.tipsRv.adapter = tipsAdapter
    }

}