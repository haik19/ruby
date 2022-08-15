package com.tbnt.ruby.ui.tips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tbnt.ruby.entity.Tips
import com.tbnt.ruby.repo.RubyDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TipsPageViewModel(private val repo: RubyDataRepo) : ViewModel() {
    private val _tipsDataFlow = MutableStateFlow<List<Tips>?>(null)
    val tipsDataFlow = _tipsDataFlow.asStateFlow()

    fun loadTipsData() = viewModelScope.launch(Dispatchers.Default) {
        repo.getData()?.let { apiModel ->
            val tipsList = mutableListOf<Tips>()
            apiModel.tips.forEach {
                tipsList.add(Tips.Title(it.name))
                it.tips.forEach { tipsContent ->
                    tipsList.add(Tips.Content(tipsContent))
                }
            }
            _tipsDataFlow.emit(tipsList)
        }
    }
}