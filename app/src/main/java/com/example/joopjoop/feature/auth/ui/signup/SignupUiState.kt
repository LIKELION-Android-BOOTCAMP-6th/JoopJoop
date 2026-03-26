package com.example.joopjoop.feature.auth.ui.signup

data class SignupUiState(
    // 입력 필드 상태
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,

    // 닉네임 중복 확인 상태
    // null: 아직 검사 안 함, true: 사용 가능, false: 중복/사용 불가
    val isNicknameAvailable: Boolean? = null,

    // 사용자에게 보여줄 안내 메시지
    val nicknameHelperMessage: String = "",

    // 버튼 활성화 여부
    val isSignupButtonEnabled: Boolean = false,

    // 회원가입 성공
    val isSignupSuccess: Boolean = false,

    // 에러 발생 시 토스트
    val errorMessage: String? = null
)