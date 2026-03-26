package com.example.joopjoop.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.MyPageRepository
import com.example.joopjoop.feature.mypage.ui.main.MyPageTab
import com.example.joopjoop.feature.mypage.ui.main.MyPageUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val myPageRepository: MyPageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    init {
        // 초기 데이터 로드 (현재 로그인한 유저 ID를 넘겨야 함)
        loadMyPageData("test_user_id")
    }

    fun loadMyPageData(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. 프로필 정보 가져오기 [F-MY-01]
            val userResult = myPageRepository.getUserProfile(userId)

            // 2. 내 쪽지 목록 가져오기 [F-MY-02]
            val postsResult = myPageRepository.getMyPosts(userId)

            // 3. 스크랩 목록 가져오기 [F-MY-03]
            val scrapsResult = myPageRepository.getMyScraps(userId)

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    user = userResult.getOrNull(),
                    myPosts = postsResult.getOrDefault(emptyList()),
                    myScraps = scrapsResult.getOrDefault(emptyList())
                )
            }
        }
    }

    fun selectTab(tab: MyPageTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}