package com.example.joopjoop.feature.auth.data.model

import androidx.compose.ui.graphics.ImageBitmap

// 사용자 정보 응답
data class UserResponse(
    val uid: String = "",
    val email: String = "",
    val nickname: String = "",
    val profileImageUrl: String? = null, // 이미지는 없을 수도 있으므로 Nullable
    val createdAt: Long = 0L // 밀리초 단위로 저장할 경우
)