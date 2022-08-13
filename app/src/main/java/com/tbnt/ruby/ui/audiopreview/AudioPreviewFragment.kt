package com.tbnt.ruby.ui.audiopreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.AudioPreviewLayoutBinding
import com.tbnt.ruby.repo.AudioDataRepo

class AudioPreviewFragment : Fragment() {

    private val audioDataRepo = AudioDataRepo()
    private val args: AudioPreviewFragmentArgs by navArgs()

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
        binding.playBtn.setTextColor("#F5C037")
        binding.playBtn.setText(getString(R.string.gen_play))
        binding.downloadBtn.setTextColor("#FFFFFF")
        binding.downloadBtn.setBgColor("#F5C037")
        binding.downloadBtn.setText(getString(R.string.gen_download))
        binding.buttonsContainer.visibility = if (args.showPlayBtn) View.GONE else View.VISIBLE

        val scrollableContent = binding.previewScrollableContent
        scrollableContent.leaveFeedbackText.setOnClickListener {
            findNavController().navigate(R.id.action_audioPreviewFragment_to_feedBackFragment)
        }
        scrollableContent.previewPlayBtn.apply {
            setTextColor("#1596C0")
            setStrokeColor("#1596C0")
            setText(getString(R.string.gen_play_preview))
            visibility =
                if (args.showPlayBtn) View.VISIBLE else View.GONE
        }

        binding.closeBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        audioDataRepo.fetchPreviewData().apply {
            scrollableContent.previewTitle.text = title
            scrollableContent.previewSubtitle.text = subTitle
            scrollableContent.ratingBar.rating = rating
            scrollableContent.previewAudioTime.text = duration
            scrollableContent.previewDesc.text = desc
            scrollableContent.descTextView.text = tips
            binding.imagePreview.apply {
                Picasso.get().load(imageUrl).into(this)
            }
        }
    }
}