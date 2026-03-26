package com.example.joopjoop.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.MyPageRepository
import com.example.joopjoop.feature.mypage.ui.main.MyPageTab
import com.example.joopjoop.feature.mypage.ui.main.MyPageUiState
import com.example.joopjoop.feature.mypage.ui.post.MyPostUiState
import com.example.joopjoop.feature.mypage.ui.scrap.MyScrapUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val myPageRepository: MyPageRepository
) : ViewModel() {

    // 1. 메인 상태 (유저 정보, 탭 등)
    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    // 2. 쪽지 상태
    private val _postUiState = MutableStateFlow(MyPostUiState())
    val postUiState = _postUiState.asStateFlow()

    // 3. 스크랩 상태
    private val _scrapUiState = MutableStateFlow(MyScrapUiState())
    val scrapUiState = _scrapUiState.asStateFlow()

    init {
        // 초기 데이터 로드 (현재 로그인한 유저 ID를 넘겨야 함)
        loadMyPageData("test_user_id")
    }

    fun loadMyPageData(userId: String) {
        viewModelScope.launch {
            // 각 상태 업데이트 로직
            val user = myPageRepository.getUserProfile(userId).getOrNull()
            val posts = myPageRepository.getMyPosts(userId).getOrDefault(emptyList())
            val scraps = myPageRepository.getMyScraps(userId).getOrDefault(emptyList())

            _uiState.update { it.copy(user = user) }
            _postUiState.update { it.copy(posts = posts) }
            _scrapUiState.update { it.copy(scraps = scraps) }
        }
    }

    fun selectTab(tab: MyPageTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}