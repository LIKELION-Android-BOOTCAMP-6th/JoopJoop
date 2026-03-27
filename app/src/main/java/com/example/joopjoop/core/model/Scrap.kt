package com.example.joopjoop.core.model

import java.util.Date

data class Scrap(
    val noteId: String = "", // Users/{userId}/scraps/{noteId} 경로의 ID
    val contentText: String = "",
    val imageUrl: String? = null,
    val createdAt: Date = Date()
)