package com.example.joopjoop.feature.mypage.data.repository

import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.Scrap
import com.example.joopjoop.core.model.User
import com.example.joopjoop.core.repository.MyPageRepository
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource

class MyPageRepositoryImpl(
    private val userSource: FirestoreUserSource
) : MyPageRepository {

    override suspend fun getUserProfile(userId: String): Result<User> {
        // TODO: 실제로는 firestoreUserSource에서 가져와야 함
        return Result.success(
            User(
                userId = userId,
                nickname = "줍줍마스터",
                profileImageUrl = "",
                noteCount = 12
            )
        )
    }

    override suspend fun getMyPosts(userId: String): Result<List<Note>> {
        // 임시 리스트 리턴
        val fakeNotes = listOf(
            Note(noteId = "1", contentText = "오늘 날씨가 좋아서 쪽지 남겨요!", userNickname = "줍줍마스터"),
            Note(noteId = "2", contentText = "여기 맛집 발견!", userNickname = "줍줍마스터")
        )
        return Result.success(fakeNotes)
    }

    override suspend fun getMyScraps(userId: String): Result<List<Scrap>> {
        // 임시 스크랩 리스트 리턴
        return Result.success(
            listOf(Scrap(noteId = "s1", contentText = "나중에 가볼 곳 스크랩"))
        )
    }
}