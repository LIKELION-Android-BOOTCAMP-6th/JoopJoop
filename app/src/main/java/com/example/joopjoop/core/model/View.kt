package com.example.joopjoop.core.model

import java.util.Date

data class View(
    val noteId: String = "",    // 조회한 note id
    val userId: String = "",    // 조회한 user id
    val viewAt: Date = Date()   // 조회한 날짜
)