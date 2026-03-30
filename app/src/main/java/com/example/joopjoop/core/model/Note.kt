package com.example.joopjoop.core.model

import java.util.Date

data class Note(
    val id: String = "", // firestore의 필드명과 통일
    val authorId: String = "",
    val userNickname: String = "",
    val userProfileImageUrl: String = "",
    val contentText: String = "",
    val thumbnailUrl: String? = null,   // 썸네일 url
    val imageUrl: String? = null,       // 이미지 url
    val category: String = "",
    val viewCount: Int = 0,
    val likeCount: Int = 0,
    val location: NoteLocation = NoteLocation(),
    val isActive: Boolean = true,
    val createdAt: Date = Date(),       // 작성 시간
    val expiresAt: Date = Date()        // 만료 시간 (3, 6, 9 ,12 시간 더한 실제시간 저장)
)

data class NoteLocation(
    val geohash: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val distance: String = ""       // 쪽지 목록에서 사용 - 해당 쪽지와 사용자의 거리 표시 데이터 ("0m")
)