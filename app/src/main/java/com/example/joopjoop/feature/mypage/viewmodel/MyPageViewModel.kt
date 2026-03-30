package com.example.joopjoop.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.MyPageRepository
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.mypage.ui.main.MyPageTab
import com.example.joopjoop.feature.mypage.ui.main.MyPageUiState
import com.example.joopjoop.feature.mypage.ui.post.MyPostUiState
import com.example.joopjoop.feature.mypage.ui.scrap.MyScrapUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val myPageRepository: MyPageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // 1. 메인 상태 (유저 정보, 탭 등)
    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = authRepository.currentUser
        .combine(_uiState) { user, state ->
            state.copy(user = user) // Auth의 최신 유저 정보를 마이페이지 상태에 주입
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MyPageUiState(isLoading = true)
        )

    // 2. 쪽지 상태
    private val _postUiState = MutableStateFlow(MyPostUiState())
    val postUiState = _postUiState.asStateFlow()

    // 3. 스크랩 상태
    private val _scrapUiState = MutableStateFlow(MyScrapUiState())
    val scrapUiState = _scrapUiState.asStateFlow()

    // 현재 세션의 유저 ID (실제로는 Auth에서 가져와야 함)
    // 2. 현재 로그인된 실제 유저 ID 저장 변수
    private var currentUserId: String = ""

    init {
        // 3. 초기화 시점에 실제 UID를 가져옴
        currentUserId = authRepository.getCurrentUid() ?: ""

        if (currentUserId.isNotEmpty()) {
            loadUserProfile(currentUserId)
            loadMyPosts(currentUserId)
        } else {
            // 로그인 정보가 없을 경우의 처리 (예: 로그인 화면 이동 등)
            android.util.Log.e("MyPageViewModel", "로그인된 유저 UID를 찾을 수 없습니다.")
        }
    }

    // 1. 유저 프로필 로드
    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = myPageRepository.getUserProfile(userId)
            _uiState.update { it.copy(
                user = result.getOrNull(),
                isLoading = false
            )}
        }
    }

    // 2. 내가 작성한 쪽지 로드
    private fun loadMyPosts(userId: String) {
        viewModelScope.launch {
            _postUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = myPageRepository.getMyPosts(userId)

            if (result.isSuccess) {
                _postUiState.update { it.copy(
                    posts = result.getOrDefault(emptyList()),
                    isLoading = false
                )}
            } else {
                _postUiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "쪽지를 불러오지 못했습니다. 다시 시도해주세요."
                )}
            }
        }
    }

    // 3. 스크랩한 쪽지 로드
    // MyPageViewModel.kt 내부
    private fun loadMyScraps(userId: String) {
        if (_scrapUiState.value.isLoading) return

        viewModelScope.launch {
            // 로딩 시작 및 이전 에러 메시지 초기화
            _scrapUiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = myPageRepository.getMyScraps(userId)

            result.onSuccess { scrapList ->
                _scrapUiState.update { it.copy(
                    scraps = scrapList,
                    isLoading = false
                )}
            }.onFailure { exception ->
                _scrapUiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "스크랩 목록을 불러오지 못했습니다."
                )}
            }
        }
    }

    // 탭 전환 처리 (Lazy Loading 핵심 로직)
    fun onTabSelected(tab: MyPageTab) {
        _uiState.update { it.copy(selectedTab = tab) }

        when (tab) {
            MyPageTab.POSTS -> {
                // 작성 쪽지가 비어있을 때만 로드 (필요시 refresh 로직 별도 구성)
                if (_postUiState.value.posts.isEmpty()) {
                    loadMyPosts(currentUserId)
                }
            }
            MyPageTab.SCRAPS -> {
                // 스크랩 탭을 처음 누르는 시점에 데이터가 없으면 로드
                if (_scrapUiState.value.scraps.isEmpty()) {
                    loadMyScraps(currentUserId)
                }
            }
        }
    }
}