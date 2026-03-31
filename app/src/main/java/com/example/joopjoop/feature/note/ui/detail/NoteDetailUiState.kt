package com.example.joopjoop.feature.note.ui.detail

data class NoteDetailUiState(
    val userId: String = "",            // 사용자 ID
    val userNickName: String = "", // 작성자 이름 (예: "김철수 디자이너")
    val createdAt: String = "", // 작성일 (예: "2023.10.27")
    val address: String = "", // 주소
    val viewCount: Int = 0,             // 조회수
    val likeCount: Int = 0,             // 좋아요 수
    val imageUrl: String? = null,       // 메인 이미지
    val profileImageUrl: String? = null, // 사용자 프로필 이미지
    val thumbnailUrl: String? = null,   // 썸네일 이미지
    val contentText: String = "",           // 쪽지 본문
    val isLiked: Boolean = false,       // 좋아요 눌렀는지
    val isBookmarked: Boolean = false,  // 스크랩 눌렀는지
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthor: Boolean = false,
)
