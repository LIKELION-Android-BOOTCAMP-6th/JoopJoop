package com.example.joopjoop.feature.auth.ui.signup

data class SignupUiState(
    // 1. 입력 필드 상태
    val nickname: String = "",
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,

    // 2. 닉네임 중복 확인 상태
    // null: 아직 검사 안 함, true: 사용 가능, false: 중복/사용 불가
    val isNicknameAvailable: Boolean? = null,

    // 3. 사용자에게 보여줄 안내 메시지
    val nicknameHelperMessage: String = "",

    // 4.  전체 로딩 상태나 버튼 활성화 여부
    val isLoading: Boolean = false,
    val isSignupButtonEnabled: Boolean = false
)