package com.example.joopjoop.feature.auth.data.model

import com.google.firebase.Timestamp

// 회원가입 요청 모델
// 사용자가 입력한 데이터 + 서버측 저장 데이터(가입일 등)를 묶는 객체
data class SignupRequest (
    val email: String,
    val nickname: String,
    val createdAt: Timestamp = Timestamp.now()
)