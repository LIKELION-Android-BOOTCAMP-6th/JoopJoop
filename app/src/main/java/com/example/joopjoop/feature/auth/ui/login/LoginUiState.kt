package com.example.joopjoop.feature.auth.ui.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val errorMessage: String? = null // 로그인 실패 시 에러 메시지
)