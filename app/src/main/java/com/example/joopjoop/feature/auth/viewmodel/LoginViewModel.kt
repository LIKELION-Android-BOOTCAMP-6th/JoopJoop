package com.example.joopjoop.feature.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.data.repository.AuthRepositoryImpl
import com.example.joopjoop.feature.auth.data.source.FirebaseAuthSource
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource
import com.example.joopjoop.feature.auth.ui.login.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val authRepository: AuthRepository = AuthRepositoryImpl(
        authSource = FirebaseAuthSource(),
        userSource = FirestoreUserSource(),
    )

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
            _uiState.update { it.copy(isLoading = true) }

            val result = authRepository.login(email, password)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                Log.d("LoginViewModel", "로그인 성공 유저 : ${_uiState.value.email}")
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, errorMessage = exception.message)
                }
                Log.d("LoginViewModel", "로그인 실패 : ${exception.message}")
            }
        }
    }
}