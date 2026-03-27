package com.example.joopjoop.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.joopjoop.core.repository.AuthRepository

//class SignupViewModelFactory(
//    private val authRepository: AuthRepository // 배달할 물건
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return SignupViewModel(authRepository) as T // 뷰모델에 꽂아줌
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}


//===========================================================================
// 1. SignupViewModel 파일에서 아래처럼 수정
// 2. 위의 주석(//)을 지워주세요
//
//class SignupViewModel : ViewModel() {
//
//    private val authRepository: AuthRepository = AuthRepositoryImpl(
//        authSource = FirebaseAuthSource(),
//        userSource = FirestoreUserSource(),
//    )