package com.tbnt.ruby.ui.auidiobooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tbnt.ruby.entity.AudioBook
import com.tbnt.ruby.getResolvedPackageId
import com.tbnt.ruby.payment.GooglePaymentService
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioBooksViewModel(
    private val repo: RubyDataRepo
) : ViewModel() {
    private val _booksDataStateFlow = MutableStateFlow<List<AudioBook>>(emptyList())
    val booksDataStateFlow = _booksDataStateFlow.asStateFlow()

    fun loadAudioBooks() = viewModelScope.launch(Dispatchers.Default) {
        repo.getData()?.let { data ->
            GooglePaymentService.queryProductDetails(data.audioBooks.map { getResolvedPackageId(it.id) }) {
                _booksDataStateFlow.emit(data.audioBooks.map { apiData ->
                    AudioBook(
                        apiData.id,
                        apiData.imageUrl,
                        apiData.name,
                        GooglePaymentService.getPrice(getResolvedPackageId(apiData.id)),
                        apiData.ratingStars,
                        isPackageBought(apiData.id),
                        apiData.isFree ?: false
                    )
                })
            }
        }
    }

    private suspend fun isPackageBought(packageId: String) =
        repo.getPurchasedIds().contains(packageId) || GooglePaymentService.isPackagePurchased(
            getResolvedPackageId(packageId)
        )

    fun downloadPackage(id: String, langCode: String) =
        viewModelScope.launch(Dispatchers.Default) {
            repo.downloadPackage(id, langCode)
        }

    fun storePurchasedData(id: List<String>) = viewModelScope.launch(Dispatchers.Default) {
        repo.storePurchasedData(id)
    }
}