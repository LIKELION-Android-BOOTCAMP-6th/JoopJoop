package com.example.joopjoop.feature.note.viewmodel

import android.os.Process.myUid
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.common.util.formatDate
import com.example.joopjoop.core.model.Scrap
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.note.ui.detail.NoteDetailUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteDetailViewModel(
    private val repository: NoteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()
    private var currentUserId: String? = null


    init {
        observeUserStatus()
    }

    private fun observeUserStatus() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                    currentUserId = user?.uid
                }
        }
    }

    // 특정 쪽지의 상세 데이터를 가져옴
    fun loadNoteDetail(noteId: String) {
        // noteId가 비어있는지 먼저 확인 로그를 찍어보세요!
        Log.d("NoteDetail", "전달받은 noteId: '$noteId'")

        if (noteId.isEmpty()) {
            Log.e("NoteDetail", "Error: noteId가 비어있습니다!")
            return
        }
        viewModelScope.launch {

            try {
                // 로딩 시작
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                // 조회수 반영된 최신 데이터 가져오기
                val noteData = repository.getNoteDetail(noteId)

                // 좋아요 상태 로드
                val isLiked = currentUserId?.let { repository.checkLikeExists(noteId, it) } ?: false

                // 쪽지 스크랩 조회
                val isBookmarked = currentUserId?.let { currentUserId ->
                    repository.isNoteBookmarked(noteId, currentUserId)
                } ?: false

                // 현재 로그인한 유저가 작성한 쪽지인지
                val currentUser = authRepository.currentUser.first()
                val myUid = currentUser?.uid

                _uiState.update {
                    it.copy(
                        isAuthor = noteData?.authorId == myUid.toString()
                    )
                }

                if (noteData != null) {
                    // 데이터가 있을 때만 조회수를 증가
                    // 조회수 +1 요청
                    repository.incrementViewCount(noteId)
                    val serverLikeCount = noteData.likeCount

                    _uiState.update {
                        it.copy(
                            userNickName = noteData.userNickname,
                            createdAt = formatDate(noteData.createdAt),
                            viewCount = noteData.viewCount,
                            likeCount = maxOf(0, serverLikeCount),
                            content = noteData.contentText,
                            imageUri = noteData.imageUrl,
                            location = noteData.location.address,
                            isLiked = isLiked,
                            isBookmarked = isBookmarked,
                            isLoading = false // 로딩 완료
                        )
                    }
                } else {
                    // 데이터가 null일 때 (ID가 존재하지 않을 때)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "쪽지를 찾을 수 없습니다.") }
                }
            } catch (e: Exception) {
                Log.e("NoteDetail", "Load Error: ${e.message}")
                _uiState.update { it.copy(isLoading = false, errorMessage = "데이터 로딩 실패") }
            }
        }
    }

    fun toggleLike(noteId: String) {
        val myId = currentUserId ?: return
        val isCurrentlyLiked = _uiState.value.isLiked
        val nextState = !isCurrentlyLiked

        // UI 상태 즉시 반영
        _uiState.update { state ->
            state.copy(
                isLiked = nextState,
                likeCount = if (nextState) state.likeCount + 1 else (state.likeCount - 1).coerceAtLeast(0)
            )
        }

        viewModelScope.launch {
            try {
                if (nextState) {
                    repository.addLike(noteId, myId)
                } else {
                    repository.removeLike(noteId, myId)
                }

                val noteData = repository.getNoteDetail(noteId)
                Log.d("NoteDetailDebug", "DB에서 가져온 이미지 URL: ${noteData?.imageUrl}")
                Log.d("NoteDetailDebug", "쪽지 작성자 ID: ${noteData?.authorId}")
                if (noteData != null) {
                    _uiState.update { state ->
                        state.copy(
                            likeCount = noteData.likeCount // 여기서 noteData.likeCount가 정확히 오는지 확인
                        )
                    }
                }
            } catch (e: Exception) {
                // 실패 시 원복
                _uiState.update { it.copy(
                    isLiked = isCurrentlyLiked,
                    likeCount = if (isCurrentlyLiked) _uiState.value.likeCount else _uiState.value.likeCount // 적절히 원복 로직 추가
                )}
            }
        }
    }

    // 스크랩 버튼 클릭 처리
    fun toggleBookmark(noteId: String) {
        val myId = currentUserId ?: return

        val isCurrentlyBookmarked = _uiState.value.isBookmarked
        val nextState = !isCurrentlyBookmarked

        viewModelScope.launch {
            try {
                if (nextState) {
                    // 스크랩 하기
                    val newBookmark = Scrap(
                        noteId = noteId,
                        contentText = _uiState.value.content,
                        imageUrl = _uiState.value.imageUri
                    )
                    repository.saveScrapNote(newBookmark, myId)
                } else {
                    // 스크랩 취소
                    repository.removeBookmark(noteId, myId)
                }
                _uiState.update { it.copy(isBookmarked = nextState) }
            } catch (e: Exception) {
                Log.e("jay", "스크랩 작업 중 오류 발생: ${e.message}")
            }
        }
    }

    // 수정 버튼 클릭 처리
    fun editNote(noteId: String) {
        // todo :: 수정 로직 추가
    }

    // 삭제 버튼 클릭 처리
    fun deleteNote(noteId: String, onSuccess: () -> Unit) {
        val myId = currentUserId ?: return
        viewModelScope.launch {
            try {
                repository.deleteNote(noteId)
                onSuccess() // 삭제 성공 시 실행할 콜백 (화면 닫기 등)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "삭제에 실패했습니다.") }
            }
        }
    }
}

