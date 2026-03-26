package com.example.joopjoop.feature.note.data.model

data class NoteDTO(
    val id: String = "",
    val authorName: String = "",
    val createdAt: String = "",
    val viewCount: Int = 0,
    val likeCount: Int = 0,
    val location: String = "",
    val imageUri: String? = null,
    val content: String = "",
    val distance: String = "",
)