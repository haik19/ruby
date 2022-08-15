package com.tbnt.ruby.ui.mediaplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tbnt.ruby.entity.AudioPlayerEntity
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MediaPlayerViewModel(private val repo: RubyDataRepo) : ViewModel() {
    private val _playerDataStateFlow = MutableStateFlow<AudioPlayerEntity?>(null)
    val playerDataStateFlow = _playerDataStateFlow.asStateFlow()

    fun loadPlayerData(id: String, index: Int) =
        viewModelScope.launch(Dispatchers.Default) {
            repo.getData()?.let { apiModel ->
                val playerData =
                    apiModel.audioBooks.find { it.id == id }?.subpackage?.getOrNull(index)?.let {
                        AudioPlayerEntity(it.imageUrl, it.name)
                    }
                _playerDataStateFlow.emit(playerData)
            }
        }
}