package com.tbnt.ruby.ui.mediaplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.MediaPlayerLayoutBinding
import com.tbnt.ruby.entity.AudioPlayerEntity
import com.tbnt.ruby.entity.State
import com.tbnt.ruby.setRoundedCorner
import com.tbnt.ruby.supportingLanCode
import com.tbnt.ruby.toPx
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*

private const val PROGRESS_UPDATE_STAMP = 100L
private const val SIMPLE = "SampleAudiobooks"
private const val AUDIOBOOKS = "Audiobooks"

class MediaPlayerFragment : Fragment(R.layout.media_player_layout) {

    private val mediaPlayerViewModel: MediaPlayerViewModel by viewModel()
    private val mediaPlayerFragmentArgs: MediaPlayerFragmentArgs by navArgs()
    private var mediaPlayer: MediaPlayer? = null
    private var viewBinding: MediaPlayerLayoutBinding? = null
    private var isPlayerDestroyed = false
    private var updateProgress = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = MediaPlayerLayoutBinding.bind(view)
        viewBinding?.let { binding ->
            setUpSeekBar()
            binding.imagePreview.setRoundedCorner(20.toPx)
            binding.backBtn.setOnClickListener {
                findNavController().popBackStack()
            }
            mediaPlayerViewModel.playerDataFlow.onEach {
                it?.let {
                    binding.retryBtn.isVisible =
                        mediaPlayerViewModel.isFileInValid(
                            resolvedUri(mediaPlayerFragmentArgs, view.context, it)
                        )
                    binding.audioTitle.text = it.title
                    Picasso.get().load(it.imageUrl).into(binding.imagePreview)
                    setUpMediaPlayer(
                        binding,
                        resolvedUri(mediaPlayerFragmentArgs, view.context, it)
                    )
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

            binding.retryBtn.setOnClickListener {
                mediaPlayerViewModel.playerDataFlow.value?.let {
                    mediaPlayerViewModel.downLoadFile(
                        resolvedUri(mediaPlayerFragmentArgs, view.context, it),
                        if (mediaPlayerFragmentArgs.isFromPreview) it.simpleAudioName else it.fullAudioName
                    ).addOnSuccessListener {
                        loadMediaData(State.RETRY)
                    }
                }
            }
            loadMediaData(State.INITIAL)
        }
    }

    private fun resolvedUri(
        args: MediaPlayerFragmentArgs,
        context: Context,
        audioPlayerEntity: AudioPlayerEntity
    ) = if (args.isFromPreview) simpleFilePathUri(context, audioPlayerEntity) else fullFilePathUri(
        context,
        audioPlayerEntity
    )

    private fun simpleFilePathUri(context: Context, audioPlayerEntity: AudioPlayerEntity) =
        Uri.parse(
            context.filesDir.absolutePath + File.separator
                    + SIMPLE + File.separator + Locale.getDefault().isO3Language.supportingLanCode() + File.separator + audioPlayerEntity.simpleAudioName.plus(
                ".mp3"
            )
        )

    private fun fullFilePathUri(context: Context, audioPlayerEntity: AudioPlayerEntity) = Uri.parse(
        context.filesDir.absolutePath + File.separator
                + AUDIOBOOKS + File.separator + Locale.getDefault().isO3Language.supportingLanCode() + File.separator + mediaPlayerFragmentArgs.audioId + File.separator + audioPlayerEntity.fullAudioName.plus(
            ".mp3"
        )
    )

    private fun setUpMediaPlayer(binding: MediaPlayerLayoutBinding, uri: Uri) {
        mediaPlayer = MediaPlayer.create(requireContext(), uri)
        mediaPlayer?.let { player ->
            binding.playAudioBtn.setOnClickListener {
                if (player.isPlaying) pauseAudio() else playAudio()
            }
            player.setOnPreparedListener {
                isPlayerDestroyed = false
                playAudio()
                binding.seekBar.max = player.duration
                binding.audioDurationTxt.text = duration(player.duration.toLong())
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        if (isPlayerDestroyed) {
                            handler.removeCallbacksAndMessages(null)
                            return
                        }
                        if (updateProgress) {
                            binding.seekBar.progress = player.currentPosition
                            binding.currentTime.text = duration(player.currentPosition.toLong())
                        }
                        handler.postDelayed(this, PROGRESS_UPDATE_STAMP)
                    }
                }, 0)
            }
        }
    }

    private fun setUpSeekBar() = viewBinding?.run {
        var audioProgress = 0
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                audioProgress = progress
                if (fromUser) {
                    updateProgress = false
                    currentTime.text = duration(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {
                updateProgress = true
                mediaPlayer?.seekTo(audioProgress)
            }
        })
    }

    private fun playAudio() = viewBinding?.run {
        playAudioBtn.isSelected = true
        mediaPlayer?.start()
    }

    private fun pauseAudio() = viewBinding?.run {
        playAudioBtn.isSelected = false
        mediaPlayer?.pause()
    }

    private fun stopRelease() = viewBinding?.run {
        isPlayerDestroyed = true
        mediaPlayer?.run {
            stop()
            reset()
            release()
        }
    }

    private fun duration(milliseconds: Long): String {
        val tempSecond = (milliseconds / 1000) % 60
        val tempMinutes = (milliseconds / (1000 * 60) % 60)
        val tempHours = (milliseconds / (1000 * 60 * 60) % 24)
        return String.format(
            "%s:%s:%s",
            displayTemple(tempHours),
            displayTemple(tempMinutes),
            displayTemple(tempSecond)
        )
    }

    private fun displayTemple(duration: Long) = when {
        duration == 0L -> "00"
        duration < 10L -> "0$duration"
        else -> duration
    }

    override fun onStop() {
        super.onStop()
        pauseAudio()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopRelease()
        viewBinding = null
    }

    private fun loadMediaData(state: State) {
        if (mediaPlayerFragmentArgs.isFromPreview) {
            mediaPlayerViewModel.loadPlayerData(
                mediaPlayerFragmentArgs.audioId,
                -1,
                state,
            )
        } else {
            //isFrom sub packages
            mediaPlayerViewModel.loadPlayerData(
                mediaPlayerFragmentArgs.audioId,
                mediaPlayerFragmentArgs.subAudioId,
                state,
            )
        }
    }

}