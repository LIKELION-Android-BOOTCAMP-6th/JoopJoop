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
                    // 실패 시: 로딩 끄고, 에러 메시지 업데이트
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "로그인에 실패했습니다."
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
}