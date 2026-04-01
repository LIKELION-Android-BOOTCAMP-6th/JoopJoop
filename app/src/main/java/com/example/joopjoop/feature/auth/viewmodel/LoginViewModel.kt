package com.example.joopjoop.feature.auth.viewmodel

import android.app.Notification
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.data.model.AuthResult
import com.example.joopjoop.feature.auth.data.repository.AuthRepositoryImpl
import com.example.joopjoop.feature.auth.data.source.FirebaseAuthSource
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource
import com.example.joopjoop.feature.auth.ui.login.LoginUiState
import com.example.joopjoop.feature.notification.viewmodel.NotificationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository, // 인터페이스를 주입받음
    private val notificationViewModel: NotificationViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // 이메일 입력 업데이트
    fun onEmailInput(email: String){
        _uiState.value = _uiState.value.copy(email = email)

    }

    // 비밀번호 입력 업데이트
    fun onPasswordInput(password: String){
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

        if(email.isBlank() || password.isBlank()) return

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
                        message.contains("auth credential is incorrect") -> "이메일 또는 비밀번호가 일치하지 않습니다."

                        // 2. 이메일 형식이 잘못된 경우
                        message.contains("invalid email") -> "유효한 이메일 형식이 아닙니다."

                        // 3. 네트워크 연결 끊김 등 기타
                        message.contains("network") -> "네트워크 연결을 확인해주세요."

                        else -> "로그인에 실패했습니다. 다시 시도해주세요."
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = friendlyMessage // 가공된 메시지 삽입
                        )
                    }
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