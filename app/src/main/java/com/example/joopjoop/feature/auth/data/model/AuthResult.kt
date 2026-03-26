package com.example.joopjoop.feature.auth.data.model

// 인증 상태 관리
sealed class AuthResult<out T>{
    // 성공 시 데이터를 담음
    data class Success<out T>(val data: T) : AuthResult<T>()

    // 실패 시 에러 내용(Throwable)
    data class Failure(val exception: Throwable) : AuthResult<Nothing>()

    // 진행 중
    object Loading : AuthResult<Nothing>()
}