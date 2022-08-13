package com.tbnt.ruby.ui.auidiobooks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.FragmentAudioBooksBinding
import com.tbnt.ruby.repo.AudioDataRepo
import com.tbnt.ruby.setRoundedCorner
import com.tbnt.ruby.toPx
import kotlin.math.abs


class AudioBooksFragment : Fragment() {

    private var deltaOffset: Float = 0f
    var verticalOffset = -1f
    private val ribbonTranslationY = 70.toPx
    private val audioDataRepo = AudioDataRepo()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_audio_books, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAudioBooksBinding.bind(view)
        binding.audioRv.apply {
            addItemDecoration(AudioRvDecoration())
            layoutManager = GridLayoutManager(context, 2)
            val audioAdapter = AudioBooksAdapter { _, _ ->
                findNavController().navigate(
                    R.id.action_audioBooksFragment_to_audioPreviewFragment
                )
            }
            binding.audioRv.adapter = audioAdapter
            audioAdapter.submitList(audioDataRepo.fetchData())
        }
        binding.fabAddOItransferIn.setRoundedCorner(26f)
        binding.audioRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                deltaOffset += dy.toFloat()
                if (deltaOffset < 0) {
                    deltaOffset = 0f
                }

                if (abs(deltaOffset) >= abs(ribbonTranslationY)) {
                    deltaOffset = abs(ribbonTranslationY)
                }

                if (verticalOffset != deltaOffset) {
                    val translationY: Float = 70.toPx - deltaOffset
                    binding.fabAddOItransferIn.translationY = translationY
                    verticalOffset = deltaOffset
                }
            }
        })
    }
}