package com.example.joopjoop.feature.auth.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.common.util.mockupseeder.Seeder
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.ui.signup.SignupUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignupViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    // 목업 쪽지 시딩 관찰용
    private val _seedingProgress = MutableStateFlow<Pair<Int, Int>?>(null) // (현재, 전체)
    val seedingProgress = _seedingProgress.asStateFlow()

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

    fun consumeErrorEvent() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // 닉네임 체크
    fun checkNickname() {
        val nickname = _uiState.value.nickname

        // 비어있지 않은지 (공백)
        if (nickname.isBlank()) {
            _uiState.update { it.copy(nicknameHelperMessage = "닉네임을 입력해주세요") }
            return
        }

        viewModelScope.launch {
            // 서버(Repository)에 중복 여부 확인 요청
            val isAvailable = authRepository.isNicknameAvailable(nickname)

            _uiState.update {
                it.copy(
                    isNicknameAvailable = isAvailable,
                    nicknameHelperMessage = if (isAvailable) "사용 가능한 닉네임입니다." else "이미 사용 중인 닉네임입니다."
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

    // 계정만들기
    fun signUp() {
        val state = _uiState.value
        Log.d("SignUp", "회원가입 시작: ${state.email}")

        viewModelScope.launch {
            // 1. 로딩 시작 알림
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Repository에 회원가입 요청
                val result = authRepository.signUp(
                    email = state.email,
                    password = state.password,
                    nickname = state.nickname
                )

                Log.d("SignUp", "결과 도착: $result")

                // AuthResult 분기처리
                when (result) {
                    is com.example.joopjoop.feature.auth.data.model.AuthResult.Success -> {
                        // 성공 시
                        Log.d("SignUp", "회원가입 성공, 가입된 유저 : ${result.data.nickname}")
                        _uiState.update {
                            it.copy(
                                isSignupSuccess = true,
                                errorMessage = "회원가입에 성공했습니다!"
                            )
                        }
                    }

                    is com.example.joopjoop.feature.auth.data.model.AuthResult.Failure -> {
                        // 실패 시
                        Log.e("SignUp", "실패: ${result.exception.message}")
                        // 1. 발생한 예외(Exception)의 종류에 따라

                        val friendlyMessage = when (result.exception) {
                            // 이미 가입된 이메일인 경우
                            is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                                "이미 가입된 이메일 주소입니다. 다른 이메일을 사용해 주세요."
                            // 이메일 형식이 잘못된 경우
                            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                                "유효하지 않은 이메일 형식입니다."
                            // 네트워크 연결이 불안정한 경우
                            is com.google.firebase.FirebaseNetworkException ->
                                "네트워크 연결이 불안정합니다. 인터넷 설정을 확인해 주세요."
                            // 그 외 알 수 없는 에러
                            else -> "회원가입 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
                        }

                        // 2. 결정된 친절한 메시지를 UI State에 업데이트합니다.
                        _uiState.update {
                            it.copy(errorMessage = friendlyMessage)
                        }
                    }

                    is com.example.joopjoop.feature.auth.data.model.AuthResult.Loading -> {
                        // 필요시 로딩 상태 UI 반영할 것
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                // 예기치 못한 크래시 방지용
                _uiState.update { it.copy(errorMessage = "알 수 없는 오류가 발생했습니다.") }
            } finally {
                // 3. 성공하든 실패하든 마지막엔 무조건 로딩을 꺼줌
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // 목업 데이터 시딩 함수
    fun runSeeding(context: Context, cycle: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            val seeder = Seeder()

            _seedingProgress.value = 0 to 0

            seeder.seedFullSeoul(context, cycle) { current, total ->
                _seedingProgress.value = current to total
            }
        }
    }
}
