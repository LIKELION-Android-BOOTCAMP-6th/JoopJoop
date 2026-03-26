package com.example.joopjoop.feature.mypage.ui.main

import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.Scrap
import com.example.joopjoop.core.model.User

data class MyPageUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val myPosts: List<Note> = emptyList(),
    val myScraps: List<Scrap> = emptyList(),
    val selectedTab: MyPageTab = MyPageTab.POSTS,
    val errorMessage: String? = null
)

enum class MyPageTab { POSTS, SCRAPS }