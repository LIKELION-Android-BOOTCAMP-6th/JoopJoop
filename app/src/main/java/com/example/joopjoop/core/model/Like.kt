package com.example.joopjoop.core.model

import java.util.Date

data class Like(
    val noteId: String = "",        // 노트 Id
    val userId: String = "",        // 좋아요를 누른 user Id
    val likedAt: Date = Date()      // 좋아요 누른 날짜
)