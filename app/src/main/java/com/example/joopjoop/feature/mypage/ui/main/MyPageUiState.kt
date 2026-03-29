package com.example.joopjoop.feature.mypage.ui.main

import com.example.joopjoop.core.model.User

data class MyPageUiState(
    val user: User? = null, // 여기에 모든 정보(닉네임, 가입일 등)가 다 들어있음
    val selectedTab: MyPageTab = MyPageTab.POSTS,
    val isLoading: Boolean = false
)

enum class MyPageTab { POSTS, SCRAPS }