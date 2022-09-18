package com.tbnt.ruby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DataViewModel(private val repo: RubyDataRepo) : ViewModel() {

    private val _dataReadyFlow = MutableStateFlow(false)
    val dataReadyFlow = _dataReadyFlow.asStateFlow()

    fun storeData(snapshot: DataSnapshot) {
        viewModelScope.launch(Dispatchers.Default) {
            repo.storeData(snapshot).collect {
                _dataReadyFlow.emit(it)
            }
        }
    }
}