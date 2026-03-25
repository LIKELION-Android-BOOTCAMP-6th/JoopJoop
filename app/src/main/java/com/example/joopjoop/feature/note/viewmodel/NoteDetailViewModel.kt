package com.example.joopjoop.feature.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.feature.note.ui.detail.NoteDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteDetailViewModel : ViewModel() {

    // UI 상태를 관리하는 StateFlow
    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    // 특정 쪽지의 상세 데이터를 가져오는 함수 (나중에 noteId를 인자로 받을 수 있습니다)
    fun loadNoteDetail(noteId: String) {
        viewModelScope.launch {

            // 테스트용 데이터 설정
            _uiState.update {
                it.copy(
                    authorName = "김철수 디자이너",
                    createdAt = "2026.03.25",
                    title = "오늘의 디자인 영감: 미니멀리즘과 공간의 미학",
                    content = "공간이 주는 여백의 미는 현대 디자인에서 가장 중요한 요소 중 하나입니다. " +
                    "단순함 속에서 발견하는 풍요로움을 김철수 디자이너와 함께 탐구해보세요. " +
                    "공간이 주는 여백의 미는 현대 디자인에서 가장 중요한 요소 중 하나입니다. " +
                    "단순함 속에서 발견하는 풍요로움을 김철수 디자이너와 함께 탐구해보세요. " +
                    "공간이 주는 여백의 미는 현대 디자인에서 가장 중요한 요소 중 하나입니다. " +
                    "단순함 속에서 발견하는 풍요로움을 김철수 디자이너와 함께 탐구해보세요. ",
                    isLiked = false,
                    isBookmarked = false
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

