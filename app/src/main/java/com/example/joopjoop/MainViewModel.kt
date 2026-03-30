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
                // TODO: [로그인 상태 관리 개선 필요]
// FirebaseAuth는 앱 삭제 후에도 로그인 상태가 복원될 수 있음 (기기/환경에 따라 다름)
// → 현재는 auth.currentUser 기준으로 로그인 여부를 판단하고 있어
//    앱 재설치 시 의도치 않게 자동 로그인되는 문제가 발생할 수 있음
//
// 해결 필요:
// 1. DataStore(또는 SharedPreferences)에 앱 기준 로그인 상태를 별도로 저장
// 2. 로그인/로그아웃 시 해당 값 업데이트
// 3. 앱 시작 시 Firebase 상태가 아닌 DataStore 값을 기준으로 로그인 여부 판단
//
// 참고:
// - Firebase Auth는 내부적으로 토큰을 유지하며 일부 기기에서는 자동 복원됨
// - Android Auto Backup, Google Play Services 상태에 따라 기기별로 다르게 동작함
                _isLoggedIn.value = (user != null)
            }
        }
    }
}