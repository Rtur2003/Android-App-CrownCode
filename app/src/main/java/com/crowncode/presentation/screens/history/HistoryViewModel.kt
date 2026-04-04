package com.crowncode.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crowncode.data.local.entity.AnalysisHistoryEntity
import com.crowncode.domain.usecase.AnalysisHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyUseCase: AnalysisHistoryUseCase
) : ViewModel() {

    val history: StateFlow<List<AnalysisHistoryEntity>> = historyUseCase
        .getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            historyUseCase.deleteEntry(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            historyUseCase.clearHistory()
        }
    }
}
