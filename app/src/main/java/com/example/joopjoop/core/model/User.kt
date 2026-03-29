package com.example.joopjoop.core.model

import java.util.Date

data class User(
    val uid: String = "",
    val email: String = "",
    val nickname: String = "",
    val profileImageUrl: String = "",
    val createdAt: Date = Date(),
    val lastCheckedAt: Date = Date(),
//    해당 변수 및 타입 확실히 정의 필요
//    val notificationCategories: List<String> = emptyList(),
    val noteCount: Int = 0 // [F-USER-01] 가입 시 0으로 초기화
)