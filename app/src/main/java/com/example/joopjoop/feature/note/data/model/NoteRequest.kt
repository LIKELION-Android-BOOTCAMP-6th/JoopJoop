package com.example.joopjoop.feature.note.data.model

data class NoteRequest(
    val authorId: String = "",
    val authorName: String = "익명",
    val content: String = "",
    val category: String = "",
    val storageHours: Int = 12,
    val imageUri: String? = null,

    // 위치 관련
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val geohash: String = "",      // 30m 반경 검색
    val location: String = "",     //주소: "OO구 OO동" 형태로

    // 시간 관련
    val createdAt: Long = System.currentTimeMillis(), // 작성 시간
    val expiresAt: Long = 0L       // 만료 시간 (createdAt + storageHours)
)