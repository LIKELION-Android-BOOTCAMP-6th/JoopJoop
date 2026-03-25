package com.example.joopjoop.feature.note.data.model

data class NoteRequest(
    val authorId: String = "",      // 작성자 ID
    val content: String = "",       // 쪽지 내용
    val category: String = "",      // 카테고리 (예: "일상")
    val storageHours: Int = 12,     // 보관 시간
    val imageUri: String? = null,   // 첨부 이미지 (없으면 null)
    val latitude: Double = 0.0,     // 쪽지 위치 위도
    val longitude: Double = 0.0,    // 쪽지 위치 경도
)