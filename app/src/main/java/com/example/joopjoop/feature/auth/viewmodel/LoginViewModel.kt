package com.example.joopjoop.feature.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.data.model.AuthResult
import com.example.joopjoop.feature.auth.ui.login.LoginUiState
import com.example.joopjoop.feature.notification.viewmodel.NotificationViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository, // 인터페이스를 주입받음
    private val notificationViewModel: NotificationViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    // 이메일 입력 업데이트
    fun onEmailInput(email: String) {
        _uiState.value = _uiState.value.copy(email = email)

    }

    // 비밀번호 입력 업데이트
    fun onPasswordInput(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    // 비밀번호 보이기/숨기기 토글 함수
    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    // 로그인 실행
    fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        // 입력 누락 시 단순 리턴이 아니라 에러 메시지를 상태에 반영
        if (email.isBlank()) {
            viewModelScope.launch { _errorEvent.emit("이메일을 입력해 주세요.") }
            return
        }
        if (password.isBlank()) {
            viewModelScope.launch { _errorEvent.emit("비밀번호를 입력해 주세요.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.login(email, password)

            when (result) {
                is AuthResult.Success -> {
                    notificationViewModel.startPeriodicNotification()
                    // 성공 시: 로딩 끄고, 성공 플래그 true
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = true
                        )
                    }
                    Log.d("LoginViewModel", "로그인 성공! 유저 이메일: ${result.data.email}")
                }

                is AuthResult.Failure -> {
                    val message = result.exception.message ?: ""
                    // 실패 시: 로딩 끄고, 에러 메시지 업데이트
                    val friendlyMessage = when {
                        // Firebase나 서버에서 내려오는 에러 메시지 키워드에 따라 분기
                        // 1. 이메일 형식 자체가 잘못된 경우 (가장 먼저 체크)
                        message.contains("invalid") && message.contains("email") ||
                                message.contains("badly formatted") -> "유효한 이메일 형식이 아닙니다."

                        // 2. 비밀번호가 틀렸거나 사용자를 찾을 수 없는 경우
                        message.contains("credential") ||
                                message.contains("password") ||
                                message.contains("user-not-found") -> "이메일 또는 비밀번호가 일치하지 않습니다."

                        // 3. 네트워크 문제
                        message.contains("network") ||
                                message.contains("timeout") -> "네트워크 연결을 확인해주세요."

                        else -> "로그인에 실패했습니다. 다시 시도해주세요."
                    }

                    // [수정] uiState를 업데이트하는 대신 SharedFlow로 에러를 던집니다.
                    _uiState.update { it.copy(isLoading = false) }
                    _errorEvent.emit(friendlyMessage)

                    Log.e("LoginViewModel", "로그인 실패: ${result.exception.message}")
                }

                is AuthResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    // 에러 메시지를 다 보여준 후 초기화하기 위한 함수 (추가 권장)
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}