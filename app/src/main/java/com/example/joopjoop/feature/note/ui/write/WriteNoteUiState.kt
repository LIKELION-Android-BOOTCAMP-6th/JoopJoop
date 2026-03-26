package com.example.joopjoop.feature.note.ui.write

data class WriteNoteUiState(
    val selectedCategory: String = "", // 카테고리 상태 : 어떤 카테고리
    val noteContent: String = "", // 내용
    val storageHours: Int = 12, // 보관시간
    val selectedImageUri: String? = null, // 나중에 사진 추가 기능을 위해 미리 준비
    val isSubmitting: Boolean = false,  // 제출 중
    val isSubmitSuccess: Boolean = false,  // 제출 성공
    val createdNoteId: String? = null, // 새로 생성된 쪽지 ID 저장용
    val errorMessage: String? = null  // 에러 메시지
    )
