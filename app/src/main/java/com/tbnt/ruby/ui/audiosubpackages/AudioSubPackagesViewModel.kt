package com.tbnt.ruby.ui.audiosubpackages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tbnt.ruby.entity.SubAudioEntity
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioSubPackagesViewModel(private val repo: RubyDataRepo) : ViewModel() {
    private val _packagesDataFlow = MutableStateFlow<List<SubAudioEntity>?>(null)
    val packagesDataFlow = _packagesDataFlow.asStateFlow()

    fun loadSubPackages(id: String) = viewModelScope.launch(Dispatchers.Default) {
        repo.getData()?.let { apiModel ->
            val subPackages = apiModel.audioBooks.find { it.id == id }?.subpackage?.map {
                SubAudioEntity(id, it.imageUrl, it.name, it.duration, it.audioFileName)
            }
            _packagesDataFlow.emit(subPackages)
        }
    }
}