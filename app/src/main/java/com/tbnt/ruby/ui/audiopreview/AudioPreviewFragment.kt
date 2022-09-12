package com.tbnt.ruby.ui.audiopreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.AudioPreviewLayoutBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPreviewFragment : Fragment() {

    private val args: AudioPreviewFragmentArgs by navArgs()
    private val audioPreviewViewModel: AudioPreviewViewModel by viewModel()
    private var audioTitle: String = ""
    private var audioPreviewId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.audio_preview_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = AudioPreviewLayoutBinding.bind(view)
        binding.run {
            playBtn.apply {
                setOnClickListener {
                    if (playBtn.getText().toString() == getString(R.string.gen_try)) {
                        //PLAY AUDIO
                        findNavController().navigate(
                            AudioPreviewFragmentDirections.actionAudioPreviewFragmentToMediaPlayerFragment(
                                audioPreviewId,
                                0, true
                            )
                        )
                    } else {
                        findNavController().navigate(
                            AudioPreviewFragmentDirections.actionAudioPreviewFragmentToAudioSubPackagesFragment(
                                audioPreviewId,
                                audioTitle
                            )
                        )
                    }
                }
                setTextColor("#F5C037")
                setText(getString(R.string.gen_try))
            }
            downloadBtn.apply {
                setTextColor("#FFFFFF")
                setBgColor("#F5C037")
                setText(getString(R.string.gen_download))
            }
            binding.buttonsContainer.visibility = if (args.showPlayBtn) View.GONE else View.VISIBLE
        }

        val scrollableContent = binding.previewScrollableContent
        scrollableContent.leaveFeedbackText.setOnClickListener {
            findNavController().navigate(R.id.action_audioPreviewFragment_to_feedBackFragment)
        }
        binding.closeBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.downloadBtn.apply {
            setOnClickListener {
                visibility = View.GONE
                showPlayButton(binding)
                audioPreviewViewModel.storePurchasedData(args.audioBookId)
            }
        }
        audioPreviewViewModel.audioPreviewFlow.onEach {
            it?.run {
                if (isPurchased) {
                    binding.downloadBtn.visibility = View.GONE
                    showPlayButton(binding)
                } else {
                    binding.downloadBtn.visibility = View.VISIBLE
                }
                audioTitle = it.title
                audioPreviewId = it.audioId
                scrollableContent.previewTitle.text = title
                scrollableContent.previewSubtitle.text =
                    getString(R.string.audio_files_count, audioFilesCount)
                scrollableContent.ratingBar.rating = rating
                scrollableContent.previewAudioTime.text = duration
                scrollableContent.previewDesc.text = desc
                scrollableContent.descTextView.text = tips
                binding.imagePreview.apply {
                    Picasso.get().load(imageUrl).into(this)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        audioPreviewViewModel.loadPreviewData(args.audioBookId)
    }

    private fun showPlayButton(binding: AudioPreviewLayoutBinding) {
        binding.playBtn.apply {
            setText(getString(R.string.gen_play))
            setTextColor("#FFFFFF")
            setBgColor("#F5C037")
        }
    }
}