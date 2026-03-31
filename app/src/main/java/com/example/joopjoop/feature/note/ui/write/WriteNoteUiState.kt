package com.example.joopjoop.feature.note.ui.write

import com.example.joopjoop.core.model.NoteLocation
import com.example.joopjoop.core.model.User
import java.util.Date

data class WriteNoteUiState(
    val user: User = User(
        uid = "",
        email = "",
        nickname = "",
        profileImageUrl = "",
        createdAt = Date(),
        lastCheckedAt = Date(),
    ),
    val selectedCategory: String = "일상",           // 카테고리 상태 : 어떤 카테고리
    val noteContent: String = "",                   // 내용
    val storageHours: Int = 12,                     // 보관시간
    val selectedImageUri: String? = null,           // 나중에 사진 추가 기능을 위해 미리 준비
    val selectedThumbnailUri: String? = null,
    val isImageUploading: Boolean = false,          // 이미지 업로드 중인지 확인
    val uploadProgress: Float = 0f,                 // 0.0 ~ 1.0 사이의 진행률 추가
    val isSubmitting: Boolean = false,              // 제출 중
    val isSubmitSuccess: Boolean = false,           // 제출 성공
    val createdNoteId: String? = null,              // 새로 생성된 쪽지 ID 저장용
    val errorMessage: String? = null,               // 에러 메시지
    val location: NoteLocation = NoteLocation(),    // 주소
)
