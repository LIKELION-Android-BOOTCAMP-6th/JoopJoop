package com.example.joopjoop.feature.mypage.ui.notes

data class MyPageNoteListUiState(
    val isLoading: Boolean = false,
    val notes: List<MyNoteCardUiModel> = emptyList(),
    val errorMessage: String? = null
)

data class MyNoteCardUiModel(
    val id: String,
    val previewText: String,
    val createdAt: String,
    val imageUrl: String?
)
