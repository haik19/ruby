package com.tbnt.ruby.ui.mediaplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tbnt.ruby.entity.AudioPlayerEntity
import com.tbnt.ruby.entity.State
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaPlayerViewModel(private val repo: RubyDataRepo) : ViewModel() {
    private val _playerDataFlow = MutableStateFlow<AudioPlayerEntity?>(null)
    val playerDataFlow = _playerDataFlow.asStateFlow()

    fun loadPlayerData(id: String, index: Int, state: State) =
        viewModelScope.launch(Dispatchers.Default) {
            repo.getData()?.let { apiModel ->
                val playerData = if (index > -1) {
                    apiModel.audioBooks.find { it.id == id }?.subpackage?.getOrNull(index)?.let {
                        AudioPlayerEntity(
                            state, it.imageUrl, it.name, fullAudioName = it.audioFileName
                        )
                    }
                } else {
                    apiModel.audioBooks.find { it.id == id }?.let {
                        AudioPlayerEntity(
                            state,
                            it.imageUrl,
                            it.name,
                            it.sampleAudioFileName
                        )
                    }
                }
                _playerDataFlow.emit(playerData?.copy(state = state))
            }
        }


    suspend fun audioNameByPosition(id: String, position: Int) = withContext(Dispatchers.IO) {
        repo.getData()?.let { apiModel ->
            apiModel.audioBooks.find { it.id == id }?.subpackage?.getOrNull(position)?.audioFileName
        }.orEmpty()
    }
}