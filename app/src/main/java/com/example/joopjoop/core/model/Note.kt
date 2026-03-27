package com.example.joopjoop.core.model

import java.util.Date

data class Note(
    val noteId: String = "",
    val userId: String = "",
    val userNickname: String = "",
    val userProfileImageUrl: String = "",
    val contentText: String = "",
    val imageUrl: String? = null,
    val category: String = "",
    val viewCount: Int = 0,
    val likeCount: Int = 0,
    // location 도 확실히 정의할 필요 있음
    val location: NoteLocation = NoteLocation(),
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val expiresAt: Date = Date()
)

data class NoteLocation(
    val geohash: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val distance: String = ""       // 쪽지 목록에서 사용 - 해당 쪽지와 사용자의 거리 표시 데이터 ("0m")
)