package com.tbnt.ruby.ui.audiopreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tbnt.ruby.entity.AudioPreviewEntity
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPreviewViewModel(private val repo: RubyDataRepo) : ViewModel() {
    private val _audioPreviewFlow = MutableStateFlow<AudioPreviewEntity?>(null)
    val audioPreviewFlow = _audioPreviewFlow.asStateFlow()

    fun loadPreviewData(id: String) = viewModelScope.launch(Dispatchers.Default) {
        repo.getData()?.let { apiModel ->
            apiModel.audioBooks.find { it.id == id }?.run {
                _audioPreviewFlow.emit(
                    AudioPreviewEntity(
                        imageUrl,
                        name,
                        audioBooksCount,
                        ratingStars,
                        duration,
                        description,
                        tips.joinToString("\n\n")
                    )
                )
            }
        }
    }
}