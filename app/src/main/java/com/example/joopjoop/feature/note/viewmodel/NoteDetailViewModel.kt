package com.example.joopjoop.feature.note.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.common.util.Util
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
            try {
                // 로딩 시작
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                // 조회수 반영된 최신 데이터 가져오기
                val noteData = repository.getNoteDetail(noteId)

                if (noteData != null) {
                    // 데이터가 있을 때만 조회수를 증가
                    // 조회수 +1 요청
                    repository.incrementViewCount(noteId)

                    _uiState.update {
                        it.copy(
                            authorName = noteData.userNickname,
                            createdAt = Util.formatDate(noteData.createdAt),
                            viewCount = noteData.viewCount,
                            likeCount = noteData.likeCount,
                            content = noteData.contentText,
                            location = noteData.location.address,
                            isLoading = false // 로딩 완료
                        )
                    }
                } else {
                    // 데이터가 null일 때 (ID가 존재하지 않을 때) (예: 가짜 마커 ID 5번 클릭 시)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "쪽지를 찾을 수 없습니다.") }
                }
            } catch (e: Exception) {
                // 네트워크 단절 등 진짜 '시스템 에러'가 발생했을 때
                Log.e("NoteDetail", "Load Error: ${e.message}")
                _uiState.update { it.copy(isLoading = false, errorMessage = "데이터 로딩 실패") }
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

