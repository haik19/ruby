package com.tbnt.ruby.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.FeedbackFragmentLayoutBinding
import com.tbnt.ruby.setRoundedCorner
import com.tbnt.ruby.toPx
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedbackFragment : Fragment() {

    private val feedbackViewModel: FeedbackViewModel by viewModel()
    private val feedbackFragmentArgs: FeedbackFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.feedback_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FeedbackFragmentLayoutBinding.bind(view)
        binding.previewImage.setRoundedCorner(20.toPx)
        feedbackViewModel.feedbackFlow.onEach {
            it?.let {
                Picasso.get().load(it.imageUrl).into(binding.previewImage)
                binding.filesCount.text = getString(R.string.audio_files_count, it.audioCunt)
                binding.packageTitle.text = it.title
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        feedbackViewModel.getFeedbackData(feedbackFragmentArgs.audioId)
        feedbackViewModel.feedbackUploadedFlow.onEach {
            if (it) findNavController().popBackStack()
            binding.progress.isVisible = false
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.submitButton.apply {
            setTextColor("#FFFFFF")
            setBgColor("#F5C037")
            setText(getString(R.string.gen_submit))
            setOnClickListener {
                binding.progress.isVisible = true
                feedbackViewModel.sendFeedBack(
                    feedbackFragmentArgs.audioId,
                    binding.ratingBar.rating,
                    binding.feedbackEditText.text?.trim().toString()
                )
                findNavController().popBackStack()
            }
        }
    }
}