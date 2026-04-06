package com.example.joopjoop.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.joopjoop.core.repository.MyPageRepository
import com.example.joopjoop.core.repository.AuthRepository

class MyPageViewModelFactory(
    private val myPageRepository: MyPageRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(myPageRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}