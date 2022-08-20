package com.tbnt.ruby.ui.auidiobooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tbnt.ruby.entity.AudioBook
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioBooksViewModel(private val repo: RubyDataRepo) : ViewModel() {
    private val _booksDataStateFlow = MutableStateFlow<List<AudioBook>>(emptyList())
    val booksDataStateFlow = _booksDataStateFlow.asStateFlow()

    fun loadAudioBooks() = viewModelScope.launch(Dispatchers.Default) {
        repo.getData()?.let {
            _booksDataStateFlow.emit(it.audioBooks.map { apiData ->
                AudioBook(apiData.id, apiData.imageUrl, apiData.name, "25$", apiData.ratingStars, repo.getPurchasedIds().contains(apiData.id))
            })
        }
    }
}