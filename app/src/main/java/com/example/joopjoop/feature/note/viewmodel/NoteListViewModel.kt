package com.example.joopjoop.feature.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.note.ui.list.NoteItem
import com.example.joopjoop.feature.note.ui.list.NoteListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteListUiState())
    val uiState: StateFlow<NoteListUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    // 테스트용 가상 데이터로 목록 불러옴
    fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val notes = repository.getNotes().map { dto ->
                NoteItem(
                    id = dto.id,
                    content = dto.content,
                    distance = dto.distance
                )
            }
            _uiState.update { it.copy(notes = notes, isLoading = false) }
        }
    }


}