package com.example.joopjoop.feature.mypage.ui.scraps

data class MyPageScrapListUiState(
    val isLoading: Boolean = false,
    val scraps: List<ScrapCardUiModel> = emptyList(),
    val errorMessage: String? = null
)

data class ScrapCardUiModel(
    val id: String,
    val sourceNoteId: String,
    val previewText: String,
    val createdAt: String,
    val imageUrl: String?
)
