package com.example.joopjoop.feature.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 로그아웃 진행 중 로딩 상태 관리
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 로그아웃 성공 시 UI에 내비게이션 신호를 보내기 위한 SharedFlow
    private val _logoutSuccess = MutableSharedFlow<Unit>()
    val logoutSuccess = _logoutSuccess.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Repository의 로그아웃 호출
                authRepository.logout()
                Log.d("SettingViewModel", "로그아웃 성공")

                // 성공 신호 전송
                _logoutSuccess.emit(Unit)
            } catch (e: Exception) {
                // 에러 처리
            } finally {
                _isLoading.value = false
            }
        }
    }
}