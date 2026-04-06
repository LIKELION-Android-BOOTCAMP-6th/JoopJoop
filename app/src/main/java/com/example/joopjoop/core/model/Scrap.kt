package com.example.joopjoop.core.model

import java.util.Date

data class Scrap(
    val noteId: String = "", // Users/{userId}/scraps/{noteId} 경로의 ID
    val contentText: String = "",
    val thumbnailUrl: String? = null,   // todo 썸네일 url로
    val createdAt: Date = Date()
)