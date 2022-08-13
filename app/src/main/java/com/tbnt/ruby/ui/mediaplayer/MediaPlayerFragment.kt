package com.tbnt.ruby.ui.mediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.tbnt.ruby.R
import com.tbnt.ruby.databinding.MediaPlayerLayoutBinding
import com.tbnt.ruby.repo.AudioDataRepo
import com.tbnt.ruby.setRoundedCorner
import com.tbnt.ruby.toPx

private const val PROGRESS_UPDATE_STAMP = 100L

class MediaPlayerFragment : Fragment(R.layout.media_player_layout) {

    private val repo = AudioDataRepo()
    private val mediaPlayerFragmentArgs: MediaPlayerFragmentArgs by navArgs()
    private var mediaPlayer: MediaPlayer? = null
    private var viewBinding: MediaPlayerLayoutBinding? = null
    private var isPlayerDestroyed = false
    private var updateProgress = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = MediaPlayerLayoutBinding.bind(view)
        viewBinding?.let { binding ->
            setUpMediaPlayer(binding)
            setUpSeekBar()
            binding.imagePreview.setRoundedCorner(20.toPx)
            binding.backBtn.setOnClickListener {
                findNavController().popBackStack()
            }
            repo.getAudioFilesData().apply {
                val currentAudion =
                    find { it.title == mediaPlayerFragmentArgs.audioTitle } ?: return
                binding.audioTitle.text = currentAudion.title
                Picasso.get().load(currentAudion.imageUrl).into(binding.imagePreview)
            }
        }
    }


    private fun setUpMediaPlayer(binding: MediaPlayerLayoutBinding) {
        mediaPlayer = MediaPlayer.create(binding.root.context, R.raw.test_audio)
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
        val tempSecond = (milliseconds / 1000).toInt() % 60
        val seconds = when {
            tempSecond == 0 -> "00"
            tempSecond < 10 -> "0$tempSecond"
            else -> tempSecond
        }
        val minutes = (milliseconds / (1000 * 60) % 60).takeIf { it > 0 } ?: "00"
        val hours = (milliseconds / (1000 * 60 * 60) % 24).takeIf { it > 0 } ?: "00"
        return String.format("%s:%s:%s", hours, minutes, seconds)
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

}