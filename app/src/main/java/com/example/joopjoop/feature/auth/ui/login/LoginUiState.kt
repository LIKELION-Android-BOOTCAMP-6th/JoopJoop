package com.example.joopjoop.feature.auth.ui.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false, // 서버랑 통신중인가?
    val errorMessage: String? = null, // 로그인 실패 시 에러 메시지
    val isLoginSuccess: Boolean = false // 로그인 성공 시 화면 이동
)