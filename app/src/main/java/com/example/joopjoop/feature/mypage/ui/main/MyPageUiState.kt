package com.example.joopjoop.feature.mypage.ui.main

import com.example.joopjoop.core.model.User

data class MyPageUiState(
    val user: User? = null,
    val selectedTab: MyPageTab = MyPageTab.POSTS,
    val isLoading: Boolean = false
)

enum class MyPageTab { POSTS, SCRAPS }