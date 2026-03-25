package com.example.joopjoop.note

data class NoteListUiState(
    val notes: List<NoteItem> = emptyList(),  // 쪽지 목록
    val isLoading: Boolean = false,            // 로딩 중 여부
)

data class NoteItem(
    val id: String = "",          // 쪽지 고유 ID
    val content: String = "",     // 쪽지 내용 미리보기 (예: "여기에 맛있는 빵집이...")
    val distance: String = "",    // 거리 (예: "100m")
)