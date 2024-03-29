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
import com.tbnt.ruby.chosenLanguage
import com.tbnt.ruby.databinding.AudioPreviewLayoutBinding
import com.tbnt.ruby.getResolvedPackageId
import com.tbnt.ruby.payment.GooglePaymentService
import com.tbnt.ruby.repo.model.FileType
import com.tbnt.ruby.toLanguageCode
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPreviewFragment : Fragment() {

    private val args: AudioPreviewFragmentArgs by navArgs()
    private val audioPreviewViewModel: AudioPreviewViewModel by viewModel()
    private var audioTitle: String = ""
    private var audioPreviewId: String = ""
    private var fullAudioFileName: String = ""
    private var simpleAudioFileName: String = ""

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
                                0, true, simpleAudioFileName, 0
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
            findNavController().navigate(
                AudioPreviewFragmentDirections.actionAudioPreviewFragmentToFeedBackFragment(
                    audioPreviewId
                )
            )
        }
        binding.closeBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.downloadBtn.apply {
            setOnClickListener {
                GooglePaymentService.launchBillingFlow(
                    requireActivity(),
                    getResolvedPackageId(args.audioBookId)
                ) {
                    visibility = View.GONE
                    showPlayButton(binding)
                    audioPreviewViewModel.storePurchasedData(listOf(args.audioBookId))
                    audioPreviewViewModel.downloadPackage(
                        args.audioBookId,
                        chosenLanguage(it.context)
                    )
                }
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
                simpleAudioFileName = it.simpleFileName
                fullAudioFileName = it.fullAudioName
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