package com.example.joopjoop.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.notification.viewmodel.NotificationViewModel
import com.example.joopjoop.feature.setting.SettingViewModel


// 인증 및 설정용 통합 팩토리로 사용중
class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val notificationViewModel: NotificationViewModel // 알림 viewmodel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // LoginViewModel 생성 요청이 들어오면
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository, notificationViewModel) as T
            }

            // SignupViewModel 생성 요청이 들어오면
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(authRepository) as T
            }
            // Logout
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(authRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}