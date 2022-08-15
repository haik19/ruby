package com.tbnt.ruby.ui.auidiobooks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.FragmentAudioBooksBinding
import com.tbnt.ruby.setRoundedCorner
import com.tbnt.ruby.toPx
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class AudioBooksFragment : Fragment() {
    private var deltaOffset: Float = 0f
    var verticalOffset = -1f
    private val ribbonTranslationY = 70.toPx
    private val audioBooksViewModel: AudioBooksViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_audio_books, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val audioAdapter = AudioBooksAdapter { id, _ ->
            val action =
                AudioBooksFragmentDirections.actionAudioBooksFragmentToAudioPreviewFragment(id)
            findNavController().navigate(action)
        }
        val binding = FragmentAudioBooksBinding.bind(view)
        binding.audioRv.apply {
            addItemDecoration(AudioRvDecoration())
            layoutManager = GridLayoutManager(context, 2)
            binding.audioRv.adapter = audioAdapter
        }

        audioBooksViewModel.booksDataStateFlow.onEach {
            if (it.isEmpty()) return@onEach
            audioAdapter.submitList(it) {
                binding.fabAddOItransferIn.visibility = View.VISIBLE
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        audioBooksViewModel.loadAudioBooks()
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