package com.example.joopjoop.feature.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.feature.note.data.repository.NoteRepository
import com.example.joopjoop.feature.note.data.repository.NoteRepositoryImpl
import com.example.joopjoop.feature.note.ui.detail.NoteDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteDetailViewModel(
    private val repository: NoteRepository = NoteRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    // 특정 쪽지의 상세 데이터를 가져옴
    fun loadNoteDetail(noteId: String) {
        viewModelScope.launch {
            val dto = repository.getNoteDetail(noteId)
            _uiState.update {
                it.copy(
                    authorName = dto.authorName,
                    createdAt = dto.createdAt,
                    viewCount = dto.viewCount + 1, // 조회수 증가
                    likeCount = dto.likeCount,
                    title = dto.title,
                    content = dto.content,
                )
            }
        }
    }

    // 좋아요 버튼 클릭 처리
    fun toggleLike() {
        _uiState.update { currentState ->
            val newLikeStatus = !currentState.isLiked
            currentState.copy(
                isLiked = newLikeStatus,
                likeCount = if (newLikeStatus) currentState.likeCount + 1 else currentState.likeCount - 1
            )
        }
    }

    // 스크랩 버튼 클릭 처리
    fun toggleBookmark() {
        _uiState.update { currentState ->
            currentState.copy(isBookmarked = !currentState.isBookmarked)
        }
    }

    // 조회수 증가
    fun incrementViewCount() {
        _uiState.update { currentState ->
            currentState.copy(viewCount = currentState.viewCount + 1)
        }
    }
}

