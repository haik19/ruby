package com.tbnt.ruby.ui.mediaplayer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.tbnt.ruby.*
import com.tbnt.ruby.databinding.MediaPlayerLayoutBinding
import com.tbnt.ruby.entity.State
import com.tbnt.ruby.repo.model.FileType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*

private const val PROGRESS_UPDATE_STAMP = 100L
private const val SIMPLE = "SampleAudiobooks"
const val AUDIOBOOKS = "Audiobooks"

class MediaPlayerFragment : Fragment(R.layout.media_player_layout) {

    private val dataViewModel: DataViewModel by sharedViewModel()
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
            binding.nextAudioBtn.setColorFilter(
                ContextCompat.getColor(
                    view.context,
                    if (mediaPlayerFragmentArgs.isFromPreview) R.color.gray_20 else R.color.gray_5
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            )
            binding.previousAudioBtn.setColorFilter(
                ContextCompat.getColor(
                    view.context,
                    if (mediaPlayerFragmentArgs.isFromPreview) R.color.gray_20 else R.color.gray_5
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            )

            binding.nextAudioBtn.isEnabled = !mediaPlayerFragmentArgs.isFromPreview
            binding.previousAudioBtn.isEnabled = !mediaPlayerFragmentArgs.isFromPreview

            setUpSeekBar()
            binding.imagePreview.setRoundedCorner(20.toPx)
            binding.backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            dataViewModel.checkFileStatus(
                generateFileId(filePackageId(), fileName()),
                resolvedUri(view.context)
            )
            dataViewModel.listenFullAudioState()

            mediaPlayerViewModel.playerDataFlow.onEach {
                it?.let {
                    binding.audioTitle.text = it.title
                    Picasso.get().load(it.imageUrl).into(binding.imagePreview)
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

            dataViewModel.fileStateFlow.onEach {
                it ?: return@onEach
                if (generateFileId(filePackageId(), fileName()) == it.id) {
                    when (it.status) {
                        is DataStatus.Failed -> {
                            binding.retryBtn.isVisible = true
                            binding.progressView.isVisible = false
                            binding.whiteLayer.isVisible = true
                        }
                        is DataStatus.Progress -> {
                            when {
                                it.progress == 1.0f -> {
                                    binding.progressView.setMaxProgress(ProgressValues.COMPLETE.value / ProgressValues.ANIMATION_LENGTH.value)
                                    binding.progressView.addAnimatorListener(object :
                                        AnimatorListenerAdapter() {
                                        override fun onAnimationEnd(animation: Animator?) {
                                            binding.retryBtn.isVisible = false
                                            binding.progressView.isVisible = false
                                            binding.whiteLayer.isVisible = false
                                            setUpMediaPlayer(
                                                binding,
                                                resolvedUri(view.context)
                                            )
                                            binding.progressView.removeAnimatorListener(this)
                                        }
                                    })
                                    binding.progressView.resumeAnimation()
                                    if (!binding.progressView.isAnimating) {
                                        handleSuccessState(view.context)
                                    }
                                }
                                it.progress == 0.0f -> {
                                    if (binding.progressView.isAnimating) return@onEach
                                    binding.retryBtn.isVisible = false
                                    binding.progressView.isVisible = true
                                    binding.whiteLayer.isVisible = true
                                    binding.progressView.setMaxProgress(ProgressValues.START.value / ProgressValues.ANIMATION_LENGTH.value)
                                    binding.progressView.playAnimation()
                                }
                                it.progress > 0.0f -> {
                                    val progressRange =
                                        ProgressValues.END.value - ProgressValues.START.value
                                    val progressFrame = progressRange * it.progress
                                    val currentFrame = progressFrame + ProgressValues.START.value
                                    binding.progressView.setMaxProgress(currentFrame / ProgressValues.ANIMATION_LENGTH.value)
                                    binding.progressView.resumeAnimation()
                                }
                            }
                        }
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

            binding.retryBtn.setOnClickListener {
                dataViewModel.downloadCurrentFile(
                    if (mediaPlayerFragmentArgs.isFromPreview) FileType.SIMPLE else FileType.FULL,
                    resolvedUri(view.context),
                    fileName(),
                    filePackageId(),
                    chosenLanguage(it.context).toLanguageCode(view.context)
                )
            }
            loadMediaData(State.INITIAL)
        }
    }

    private fun handleSuccessState(context: Context) = viewBinding?.run {
        retryBtn.isVisible = false
        progressView.isVisible = false
        setUpMediaPlayer(
            this,
            resolvedUri(context)
        )
    }

    private fun resolvedUri(
        context: Context
    ) = if (mediaPlayerFragmentArgs.isFromPreview) simpleFilePathUri(
        context,
        mediaPlayerFragmentArgs.simpleAudioName
    ) else fullFilePathUri(
        context,
        mediaPlayerFragmentArgs.fullAudioName
    )

    private fun simpleFilePathUri(context: Context, simpleAudioName: String) =
        Uri.parse(
            context.filesDir.absolutePath + File.separator
                    + SIMPLE + File.separator + Locale.getDefault().isO3Language.supportingLanCode() + File.separator + simpleAudioName.plus(
                ".mp3"
            )
        )

    private fun fullFilePathUri(context: Context, fullAudioName: String) = Uri.parse(
        context.filesDir.absolutePath + File.separator
                + AUDIOBOOKS + File.separator + Locale.getDefault().isO3Language.supportingLanCode() + File.separator + mediaPlayerFragmentArgs.audioId + File.separator + fullAudioName.plus(
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

    private fun filePackageId() =
        mediaPlayerFragmentArgs.audioId.takeIf { mediaPlayerFragmentArgs.isFromPreview }
            ?: mediaPlayerFragmentArgs.audioId

    private fun fileName() =
        mediaPlayerFragmentArgs.simpleAudioName
            .takeIf { mediaPlayerFragmentArgs.isFromPreview }
            ?: mediaPlayerFragmentArgs.fullAudioName
}