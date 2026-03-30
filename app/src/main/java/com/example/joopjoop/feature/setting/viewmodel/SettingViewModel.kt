package com.example.joopjoop.feature.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.common.util.ImageProcessor
import com.example.joopjoop.core.model.User
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.data.model.AuthResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 현재 사용자 정보 (AuthRepository의 Flow를 관찰하여 실시간 반영)
    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // 로딩 상태 관리
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // UI 이벤트 (로그아웃 성공, 수정 성공 등)
    private val _settingEvent = MutableSharedFlow<SettingEvent>()
    val settingEvent = _settingEvent.asSharedFlow()

    /**
     * [F-MY-04] 닉네임 변경 요청
     */
    // 닉네임 사용 가능 여부 (null: 확인 전, true: 사용 가능, false: 중복/사용 불가)
    private val _isNicknameAvailable = MutableStateFlow<Boolean?>(null)
    val isNicknameAvailable = _isNicknameAvailable.asStateFlow()

    // 사용자가 닉네임을 입력 중일 때 상태 초기화
    fun onNicknameChanged() {
        _isNicknameAvailable.value = null
    }

    // [중복 확인 실행]
    fun checkNicknameAvailability(nickname: String) {
        if (nickname.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            // 레포지토리에 있는 함수 사용 (사용 가능하면 true 반환)
            val available = authRepository.isNicknameAvailable(nickname)
            _isNicknameAvailable.value = available
            _isLoading.value = false
        }
    }

    fun updateNickname(newNickname: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.updateProfile(newNickname = newNickname)

            when (result) {
                is AuthResult.Success -> {
                    Log.d("SettingViewModel", "닉네임 수정 성공: $newNickname")
                    _settingEvent.emit(SettingEvent.UpdateSuccess)
                }
                is AuthResult.Failure -> {
                    Log.e("SettingViewModel", "수정 실패: ${result.exception.message}")
                    _settingEvent.emit(SettingEvent.Error(result.exception.message ?: "수정 중 오류 발생"))
                }
                else -> {
                    Log.d("SettingViewModel", "기타 상태(예: Loading) 무시")
                }
            }
            _isLoading.value = false
        }
    }

//    // 프로필 사진 변경
//    fun updateProfileImage(imageUri: android.net.Uri) {
//        viewModelScope.launch {
//            _isLoading.value = true
//
//            try {
//                // ImageProcessor로 이미지 압축 (ByteArray 추출)
//                val compressedBytes = imageProcessor.processOriginal(imageUri)
//
//                if (compressedBytes == null) {
//                    _settingEvent.emit(SettingEvent.Error("이미지 가공 실패"))
//                    return@launch
//                }
//
//                // 압축된 데이터를 서버에 업로드 (ByteArray 기반 업로드)
//                // authRepository에 uploadProfileImage(ByteArray) 함수가 있다고 가정합니다.
//                val uploadResult = authRepository.uploadProfileImage(compressedBytes)
//
//                when (uploadResult) {
//                    is AuthResult.Success<*> -> {
//                        val newUrl = uploadResult.data
//                        // 3. Firestore 프로필 URL 업데이트
//                        val currentNickname = currentUser.value?.nickname ?: ""
//                        authRepository.updateProfile(
//                            newNickname = currentNickname,
//                            newImageUrl = newUrl as String?
//                        )
//                        _settingEvent.emit(SettingEvent.UpdateSuccess)
//                    }
//
//                    is AuthResult.Failure -> {
//                        _settingEvent.emit(SettingEvent.Error("업로드 실패: ${uploadResult.exception.message}"))
//                    }
//
//                    else -> {}
//                }
//            } catch (e: Exception) {
//                _settingEvent.emit(SettingEvent.Error("오류 발생: ${e.message}"))
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

    /**
     * 로그아웃 실행
     */
    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.logout()
                Log.d("SettingViewModel", "로그아웃 성공")
                _settingEvent.emit(SettingEvent.LogoutSuccess)
            } catch (e: Exception) {
                Log.e("SettingViewModel", "로그아웃 에러: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// 설정 화면에서 발생하는 이벤트 정의
sealed class SettingEvent {
    object LogoutSuccess : SettingEvent()
    object UpdateSuccess : SettingEvent()
    data class Error(val message: String) : SettingEvent()
}