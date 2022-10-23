package com.tbnt.ruby

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.tbnt.ruby.repo.RubyDataRepo
import com.tbnt.ruby.repo.model.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DataViewModel(private val repo: RubyDataRepo) : ViewModel() {

    private val _dataReadyFlow = MutableStateFlow(false)
    val dataReadyFlow = _dataReadyFlow.asStateFlow()

    private val _fileStateFlow = MutableStateFlow<DataState?>(null)
    val fileStateFlow = _fileStateFlow.asStateFlow()

    fun storeData(snapshot: DataSnapshot) {
        viewModelScope.launch(Dispatchers.Default) {
            val dataFlow = repo.storeData(snapshot) {
                _fileStateFlow.tryEmit(it)
            }
            dataFlow.collect {
                _dataReadyFlow.emit(it)
            }
        }
    }

    fun checkFileStatus(fileId: String, uri: Uri) = viewModelScope.launch {
        repo.checkFileStatus(fileId, uri).collect {
            _fileStateFlow.tryEmit(it)
        }
    }

    fun listenFullAudioState() {
        repo.listenFullAudioState {
            _fileStateFlow.tryEmit(it)
        }
    }

    fun downloadCurrentFile(
        fileType: FileType,
        uri: Uri,
        fileName: String,
        packageId: String,
        audioChosenLanguage: String,
    ) {
        viewModelScope.launch {
            repo.downloadCurrentFile(fileType, uri, fileName, packageId, audioChosenLanguage) {
                _fileStateFlow.tryEmit(it)
            }
        }
    }
}