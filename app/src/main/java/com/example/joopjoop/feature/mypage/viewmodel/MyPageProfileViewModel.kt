package com.example.joopjoop.feature.mypage.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.joopjoop.feature.mypage.data.repository.MyPageRepository
import com.example.joopjoop.feature.mypage.data.repository.MyPageRepositoryImpl
import com.example.joopjoop.feature.mypage.ui.profile.MyPageProfileUiState

class MyPageProfileViewModel(
    private val repository: MyPageRepository = MyPageRepositoryImpl()
) : ViewModel() {

    var uiState: MyPageProfileUiState by mutableStateOf(MyPageProfileUiState(isLoading = true))
        private set

    fun loadProfile() {
        runCatching {
            repository.getProfile()
        }.onSuccess { profile ->
            uiState = MyPageProfileUiState(
                isLoading = false,
                nickname = profile.nickname,
                noteCount = profile.noteCount,
                profileImageUrl = profile.profileImageUrl,
                errorMessage = null
            )
        }.onFailure {
            uiState = MyPageProfileUiState(
                isLoading = false,
                errorMessage = "프로필을 불러오지 못했습니다."
            )
        }
    }
}
