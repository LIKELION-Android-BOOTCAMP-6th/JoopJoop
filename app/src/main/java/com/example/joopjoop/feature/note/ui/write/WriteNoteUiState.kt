package com.example.joopjoop.feature.note.ui.write

data class WriteNoteUiState(
    val selectedCategory: String = "일상", // 카테고리 상태 : 어떤 카테고리
    val noteContent: String = "", // 내용
    val storageHours: Int = 12, // 보관시간
    val selectedImageUri: String? = null // 나중에 사진 추가 기능을 위해 미리 준비
)