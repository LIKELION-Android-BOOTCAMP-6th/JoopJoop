package com.example.joopjoop.feature.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.feature.note.data.model.NoteRequest
import com.example.joopjoop.feature.note.data.repository.NoteRepository
import com.example.joopjoop.feature.note.data.repository.NoteRepositoryImpl
import com.example.joopjoop.feature.note.ui.write.WriteNoteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WriteNoteViewModel(
    private val repository: NoteRepository = NoteRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteNoteUiState())
    val uiState: StateFlow<WriteNoteUiState> = _uiState.asStateFlow()

    // 카테고리 목록
    val categories = listOf("일상", "감성", "추억", "맛집")

    // 카테고리 선택
    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    // 내용 입력 (200자 제한)
    fun onContentChange(content: String) {
        if (content.length <= 300) {
            _uiState.update { it.copy(noteContent = content) }
        }
    }

    private val timeOptions = listOf(3, 6, 12, 24)

    // 보관시간 증가
    fun increaseHours() {
        val currentIndex = timeOptions.indexOf(_uiState.value.storageHours)
        // 마지막 인덱스보다 작을 때만 다음 값으로 업데이트
        if (currentIndex < timeOptions.size - 1) {
            updateHours(timeOptions[currentIndex + 1])
        }
    }

    // 보관시간 감소 (최소 3시간)
    fun decreaseHours() {
        val currentIndex = timeOptions.indexOf(_uiState.value.storageHours)
        // 0보다 클 때만 이전 값으로 업데이트
        if (currentIndex > 0) {
            updateHours(timeOptions[currentIndex - 1])
        }
    }

    private fun updateHours(newHours: Int) {
        _uiState.update { it.copy(storageHours = newHours) }
    }

    // 이미지 선택
    fun onImageSelected(uri: String?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    // 제출 가능 여부 (내용이 있어야 제출 가능)
    val isSubmitEnabled: Boolean
        get() = _uiState.value.noteContent.isNotBlank()

    // 쪽지 제출
    fun submitNote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            try {
                val request = NoteRequest(
                    content = _uiState.value.noteContent,
                    category = _uiState.value.selectedCategory,
                    storageHours = _uiState.value.storageHours,
                    imageUri = _uiState.value.selectedImageUri
                )

                // 성공 시 ID를 받아옴
                val newId = repository.createNote(request)

                _uiState.update {
                    it.copy(
                        isSubmitSuccess = true,
                        createdNoteId = newId
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }


   // 에러 메시지 초기화
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // 작성 초기화 - 쪽지 제출 성공 후 화면을 초기 상태로..
    fun resetNote() {
        _uiState.update { WriteNoteUiState() }
    }
            }