package com.example.joopjoop.feature.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.note.ui.detail.NoteDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteDetailViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    // 특정 쪽지의 상세 데이터를 가져옴
    fun loadNoteDetail(noteId: String) {
        viewModelScope.launch {
            // 조회수 +1 요청
            repository.incrementViewCount(noteId)
            // 조회수 반영된 최신 데이터 가져오기
            val dto = repository.getNoteDetail(noteId)
            _uiState.update {
                it.copy(
                    authorName = dto.authorName,
                    createdAt = dto.createdAt,
                    viewCount = dto.viewCount,
                    likeCount = dto.likeCount,
                    content = dto.content,
                    location = dto.location
                )
            }
        }
    }

    // 좋아요 버튼 클릭 처리
    fun toggleLike(noteId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val isNowLiked = !currentState.isLiked

            // +1 or -1
            val amount = if (isNowLiked) 1 else -1

            repository.updateLikeCount(noteId, amount)
            _uiState.update { state ->
                state.copy(
                    isLiked = isNowLiked,
                    likeCount = state.likeCount + amount
                )
            }
        }
    }

    // 스크랩 버튼 클릭 처리
    fun toggleBookmark() {
        _uiState.update { currentState ->
            currentState.copy(isBookmarked = !currentState.isBookmarked)
        }
    }
}

