package com.example.joopjoop.feature.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.MyPageRepository
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.mypage.ui.main.MyPageTab
import com.example.joopjoop.feature.mypage.ui.main.MyPageUiState
import com.example.joopjoop.feature.mypage.ui.post.MyPostUiState
import com.example.joopjoop.feature.mypage.ui.scrap.MyScrapUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    fun getCurrentUid(): String = currentUserId


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
    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. 기존 유저 정보 가져오기
            val result = myPageRepository.getUserProfile(userId)
            val user = result.getOrNull()
            Log.d("MyPageVM", "유저 정보 가져옴: ${user?.nickname}")

            // 2. 게시물 개수 따로 가져오기
            // 만약 repository가 다르다면 주입받아서 사용하세요.
            val noteCount = authRepository.getUserNoteCount(userId)
            Log.d("MyPageVM", "가져온 게시물 개수: $noteCount")

            // 3. UI State 업데이트 (user 객체에 개수를 copy해서 넣어줌)
            _uiState.update { it.copy(
                user = user?.copy(noteCount = noteCount), // User 클래스에 noteCount 필드가 있어야 함
                noteCount = noteCount,
                isLoading = false
            )}
        }
    }
    fun loadMyPosts(userId: String) {
        viewModelScope.launch {
            _postUiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. 데이터 가져오기 및 정렬 작업을 백그라운드에서 수행
            val sortedPosts = withContext(Dispatchers.IO) {
                val result = myPageRepository.getMyPosts(userId)
                result.getOrNull()?.sortedByDescending { it.createdAt } // createdAt 기준 내림차순
            }

            // 2. 결과 UI 반영 (Main)
            if (sortedPosts != null) {
                _postUiState.update { it.copy(
                    posts = sortedPosts,
                    isLoading = false
                )}
            } else {
                _postUiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "쪽지를 불러오지 못했습니다."
                )}
            }
        }
    }

    fun loadMyScraps(userId: String) {
        if (_scrapUiState.value.isLoading) return

        viewModelScope.launch {
            _scrapUiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. 데이터 가져오기 및 정렬 (IO)
            val sortedScraps = withContext(Dispatchers.IO) {
                val result = myPageRepository.getMyScraps(userId)
                // 스크랩 데이터 내부에 날짜 필드(createdAt 또는 timestamp)가 있어야 합니다.
                result.getOrNull()?.sortedByDescending { it.createdAt }
            }

            // 2. 결과 UI 반영 (Main)
            if (sortedScraps != null) {
                _scrapUiState.update { it.copy(
                    scraps = sortedScraps,
                    isLoading = false
                )}
            } else {
                _scrapUiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "스크랩 목록을 불러오지 못했습니다."
                )}
            }
        }
    }
    // 2. 내가 작성한 쪽지 로드
    /*fun loadMyPosts(userId: String) {
        viewModelScope.launch {
            _postUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = myPageRepository.getMyPosts(userId)

            if (result.isSuccess) {
                val sortedPosts = result.getOrDefault(emptyList())
                    .sortedByDescending { it.createdAt }


                _postUiState.update { it.copy(
                    posts = sortedPosts,
                    isLoading = false
                )}
            } else {
                _postUiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "쪽지를 불러오지 못했습니다. 다시 시도해주세요."
                )}
            }
        }
    }*/

    // 3. 스크랩한 쪽지 로드
    // MyPageViewModel.kt 내부
    /*fun loadMyScraps(userId: String) {
        if (_scrapUiState.value.isLoading) return

        viewModelScope.launch {
            // 로딩 시작 및 이전 에러 메시지 초기화
            _scrapUiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = myPageRepository.getMyScraps(userId)

            result.onSuccess { scrapList ->
                val sortedScraps = scrapList.sortedByDescending { it.createdAt }

                _scrapUiState.update { it.copy(
                    scraps = sortedScraps,
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
*/
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
    // UI에서 호출할 수 있도록 refresh 함수 추가
    fun refreshAllData() {
        val userId = authRepository.getCurrentUid() ?: ""
        if (userId.isNotEmpty()) {
            currentUserId = userId
            loadUserProfile(userId)
            loadMyPosts(userId)
            loadMyScraps(userId)
        }
    }
    fun getCurrentUserId(): String {
        return currentUserId
    }
}