package com.tbnt.ruby.ui.myaudiobooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tbnt.ruby.entity.MyAudioBook
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyAudioBooksViewModel(private val repo: RubyDataRepo) : ViewModel() {

    private val _myAudioBooksFlow = MutableStateFlow<List<MyAudioBook>?>(null)
    val myAudioBooksFlow = _myAudioBooksFlow.asStateFlow()

    fun loadMyAudioBooks() = viewModelScope.launch(Dispatchers.Default) {
        repo.gePurchasedData()?.let { apiModel ->
            val myAudioBooks = mutableListOf<MyAudioBook>()
            apiModel.audioBooks.forEach { audioBook ->
                myAudioBooks.add(
                    MyAudioBook(
                        audioBook.id,
                        audioBook.imageUrl,
                        audioBook.name,
                        audioBook.duration,
                        audioBook.ratingStars
                    )
                )
            }
            _myAudioBooksFlow.emit(myAudioBooks)
        }
    }
}