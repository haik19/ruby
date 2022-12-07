package com.tbnt.ruby.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tbnt.ruby.entity.FeedBackEntity
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedbackViewModel(private val repo: RubyDataRepo) : ViewModel() {

    private val _feedbackFlow = MutableStateFlow<FeedBackEntity?>(null)
    val feedbackFlow = _feedbackFlow.asStateFlow()

    private val _feedbackUploadedFlow = MutableStateFlow<Boolean>(false)
    val feedbackUploadedFlow = _feedbackUploadedFlow.asStateFlow()

    fun getFeedbackData(id: String) = viewModelScope.launch(Dispatchers.Default) {
        repo.getData()?.let { apiModel ->
            apiModel.audioBooks.find { it.id == id }?.run {
                _feedbackFlow.update {
                    FeedBackEntity(imageUrl, name, audioBooksCount)
                }
            }
        }
    }

    fun sendFeedBack(
        packageId: String,
        rating: Float,
        feedback: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        repo.sendFeedback(packageId, rating, feedback) {
            _feedbackUploadedFlow.tryEmit(true)
        }
    }
}