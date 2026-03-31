package com.example.joopjoop.core.model

import java.util.Date

data class Note(
    val id: String = "",                // note 문서 id
    val authorId: String = "",          // 유저 uid
    val userNickname: String = "",      // 유저 닉네임
    val profileImageUrl: String? = "",   // 유저 프로필 사진
    val contentText: String = "",       // 쪽지 내용
    val thumbnailUrl: String? = null,   // 썸네일 url
    val imageUrl: String? = null,       // 이미지 url
    val category: String = "",          // 카테고리
    val viewCount: Int = 0,             // 조회수
    val likeCount: Int = 0,             // 좋아요
    val location: NoteLocation = NoteLocation(),    // 위치
    val isActive: Boolean = true,       // 쪽지 노출 여부
    val storageHours: Int = 12,         // 보관 시간
    val createdAt: Date = Date(),       // 작성 시간
    val expiresAt: Date = Date()        // 만료 시간 (3, 6, 9 ,12 시간 더한 실제시간 저장)
)

// 위치 데이터 클래스
data class NoteLocation(
    val geohash: String = "",      // 쪽지 위치 geoHash
    val latitude: Double = 0.0,    // 위도
    val longitude: Double = 0.0,   // 경도
    val address: String = "",      // 쪽지 위치 주소
    val distance: String = ""      // 쪽지 거리 - 해당 쪽지와 사용자의 거리 표시 데이터 ("0m")
)