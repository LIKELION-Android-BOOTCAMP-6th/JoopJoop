package com.example.joopjoop.feature.mypage.ui.profile

data class MyPageProfileUiState(
    val isLoading: Boolean = false,
    val nickname: String = "",
    val noteCount: Int = 0,
    val profileImageUrl: String? = null,
    val errorMessage: String? = null
)
