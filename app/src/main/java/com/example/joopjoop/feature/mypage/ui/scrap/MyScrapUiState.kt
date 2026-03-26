package com.example.joopjoop.feature.mypage.ui.scrap

import com.example.joopjoop.core.model.Note

data class MyScrapUiState(
    val scraps: List<Note> = emptyList(),
    val isLoading: Boolean = false
)