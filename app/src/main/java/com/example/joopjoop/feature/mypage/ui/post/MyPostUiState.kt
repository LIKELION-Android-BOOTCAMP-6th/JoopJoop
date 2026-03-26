package com.example.joopjoop.feature.mypage.ui.post

import com.example.joopjoop.core.model.Note

data class MyPostUiState(
    val posts: List<Note> = emptyList(),
    val isLoading: Boolean = false
)