package com.example.joopjoop.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.data.repository.AuthRepositoryImpl
import com.example.joopjoop.feature.auth.data.source.FirebaseAuthSource
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource
import com.example.joopjoop.feature.auth.ui.signup.SignupUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepositoryImpl(
        authSource = FirebaseAuthSource(),
        userSource = FirestoreUserSource(),
    )

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    // 닉네임 입력
    fun onNicknameInput(nickname: String) {
        // 공백을 제거한 텍스트만 받도록 필터링
        val filteredNickname = nickname.filter { !it.isWhitespace() }

        _uiState.update {
            it.copy(
                nickname = filteredNickname,
                isNicknameAvailable = null, // 새로운 입력이 들어오면 다시 검사해야 함
                nicknameHelperMessage = ""
            )
        }
        checkSignupButtonEnabled() // 회원가입 버튼 활성화 확인
    }

    // 이메일 입력
    fun onEmailInput(email: String) {
        _uiState.update { it.copy(email = email) }
        checkSignupButtonEnabled() // 회원가입 버튼 활성화 확인
    }

    // 비밀번호 입력
    fun onPasswordInput(password: String) {
        _uiState.update { it.copy(password = password) }
        checkSignupButtonEnabled() // 회원가입 버튼 활성화 확인
    }

    // 비밀번호 표시 여부
    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }
    fun signUp() {
        val state = _uiState.value

        viewModelScope.launch {
            // Repository에 회원가입 요청
            val result = authRepository.signUp(
                email = state.email,
                password = state.password,
                nickname = state.nickname
            )

            result.onSuccess {
                // 성공 로직 (가입 성공 토스트)
                _uiState.update { it.copy(errorEvent = "회원가입에 성공했습니다!") }
            }.onFailure { exception ->
                // 실패 로직 (가입 실패 토스트)
                // 에러 메시지를 errorEvent에 담음
                _uiState.update {
                    it.copy(errorEvent = exception.message ?: "회원가입 실패")
                }
            }
        }
    }

    fun consumeErrorEvent() {
        _uiState.update { it.copy(errorEvent = null) }
    }
    // 닉네임 체크
    fun checkNickname(){
        val nickname = _uiState.value.nickname

        // 비어있지 않은지 (공백)
        if (nickname.isBlank()){
            _uiState.update { it.copy(nicknameHelperMessage = "닉네임을 입력해주세요")}
            return
        }

        viewModelScope.launch {
            // 서버(Repository)에 중복 여부 확인 요청
            val isAvailable = authRepository.isNicknameAvailable(nickname)

            _uiState.update {
                it.copy(
                    isNicknameAvailable = isAvailable,
                    nicknameHelperMessage = if(isAvailable) "사용 가능한 닉네임입니다." else "이미 사용 중인 닉네임입니다."
                )
            }
            // 중복 결과가 나왔으므로 버튼 활성화 여부 다시 계산
            checkSignupButtonEnabled()
        }
    }

    // 버튼 활성화 조건 확인
    private fun checkSignupButtonEnabled() {
        val state = _uiState.value
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()
        val isPasswordValid = state.password.length >= 8 // 비밀번호 길이 조건
        val isNicknameValid = state.nickname.isNotBlank() && state.isNicknameAvailable == true

        // 모든 조건이 참(true)일 때만 회원가입 버튼 활성화
        _uiState.update {
            it.copy(
                isSignupButtonEnabled = isEmailValid && isPasswordValid && isNicknameValid
            )
        }
    }
}
