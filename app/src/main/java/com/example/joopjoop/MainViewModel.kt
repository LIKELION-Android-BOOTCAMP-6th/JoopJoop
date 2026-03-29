package com.example.joopjoop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // null: 아직 확인 중 (스플래시/로딩)
    // true: 로그인 됨
    // false: 로그인 안 됨
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            // AuthRepository의 currentUser Flow를 관찰
            authRepository.currentUser.collect { user ->
                _isLoggedIn.value = (user != null)
            }
        }
    }
}