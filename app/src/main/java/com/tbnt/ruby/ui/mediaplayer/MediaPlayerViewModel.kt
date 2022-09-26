package com.tbnt.ruby.ui.mediaplayer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tbnt.ruby.entity.AudioPlayerEntity
import com.tbnt.ruby.entity.State
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

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


    fun isFileInValid(uri: Uri) = File(uri.path.orEmpty()).length() == 0L

    fun downLoadFile(uri: Uri, fileName: String) = repo.downloaSimpledFile(uri, fileName)
}