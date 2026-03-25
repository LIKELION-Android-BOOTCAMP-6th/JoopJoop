package com.example.joopjoop.feature.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.feature.note.ui.list.NoteItem
import com.example.joopjoop.feature.note.ui.list.NoteListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NoteListUiState())
    val uiState: StateFlow<NoteListUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    // 테스트용 가상 데이터로 목록 불러오기
    private fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val mockNotes = List(20) { index ->
                NoteItem(
                    id = index.toString(),
                    content = "맛있는 빵집 정보를 공유합니다!",
                    distance = "100m"
                )
            }

            _uiState.update { it.copy(notes = mockNotes, isLoading = false) }
        }
    }

    // 쪽지 목록 업데이트 (나중에 서버에서 데이터 받아올 때 사용)
    fun updateNotes(notes: List<NoteItem>) {
        _uiState.update { it.copy(notes = notes) }
    }

    // 로딩 상태 변경
    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}