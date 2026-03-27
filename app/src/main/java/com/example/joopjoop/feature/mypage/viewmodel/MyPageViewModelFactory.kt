package com.example.joopjoop.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.joopjoop.core.repository.MyPageRepository

class MyPageViewModelFactory(
    private val myPageRepository: MyPageRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(myPageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}