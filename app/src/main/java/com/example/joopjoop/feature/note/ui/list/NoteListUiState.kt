package com.example.joopjoop.feature.note.ui.list

data class NoteListUiState(
    val notes: List<NoteItem> = emptyList(),  // 쪽지 목록
    val isLoading: Boolean = false,            // 로딩 중 여부
    val distance: String = "",     // 거리
    val myLatitude: Double = 0.0,  // 내 현재 위도
    val myLongitude: Double = 0.0  // 내 현재 경도

)

data class NoteItem(
    val id: String = "",          // 쪽지 고유 ID
    val authorName: String = "",  // 작성자 이름
    val content: String = "",     // 쪽지 내용 미리보기 (예: "여기에 맛있는 빵집이...")
    val distance: String = "",    // 거리 (예: "100m")
    val isWithinRange: Boolean,   // 반경 이내 여부
    val latitude: Double,         // 위도
    val longitude: Double         // 경도
)