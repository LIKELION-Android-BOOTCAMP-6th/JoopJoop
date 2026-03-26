package com.example.joopjoop.feature.mypage.data.repository

import com.example.joopjoop.feature.mypage.data.model.MyNoteSummary
import com.example.joopjoop.feature.mypage.data.model.ProfileSummary
import com.example.joopjoop.feature.mypage.data.model.ScrapSummary

/**
 * Temporary repository that combines auth-like and note-like data.
 * No external data source is connected yet.
 */
class MyPageRepositoryImpl : MyPageRepository {

    override fun getProfile(): ProfileSummary {
        return ProfileSummary(
            userId = "demo-user-92",
            nickname = "줍줍이_92",
            noteCount = 100,
            profileImageUrl = null
        )
    }

    override fun getMyNotes(): List<MyNoteSummary> {
        return listOf(
            MyNoteSummary(
                noteId = "note-1",
                previewText = "오늘은 날씨가 너무 좋아서 산책을 다녀왔다.",
                createdAt = "24.11.20",
                imageUrl = null
            ),
            MyNoteSummary(
                noteId = "note-2",
                previewText = "퇴근길에 본 하늘은 보라색이었다. 잊지 않으려고 기록한다.",
                createdAt = "24.11.18",
                imageUrl = null
            )
        )
    }

    override fun getScraps(): List<ScrapSummary> {
        return listOf(
            ScrapSummary(
                scrapId = "scrap-1",
                sourceNoteId = "note-55",
                previewText = "작은 것들로부터 얻는 커다란 행복",
                createdAt = "24.11.21",
                imageUrl = null
            ),
            ScrapSummary(
                scrapId = "scrap-2",
                sourceNoteId = "note-61",
                previewText = "Playlist #04",
                createdAt = "24.11.19",
                imageUrl = null
            )
        )
    }
}
