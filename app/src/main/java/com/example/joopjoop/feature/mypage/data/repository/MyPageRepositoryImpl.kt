package com.example.joopjoop.feature.mypage.data.repository

import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
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
                uid = userId,
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

    override suspend fun getMyScraps(userId: String): Result<List<Note>> {
        // VO 구조에 맞춘 임시 데이터 리스트
        val dummyScraps = listOf(
            // 1. 이미지가 있는 쪽지 (이미지 예시 좌측 상단)
            Note(
                noteId = "s1",
                userId = userId,
                userNickname = "줍줍이_92",
                contentText = "오늘은 날씨가 너무 좋아서 산책을 다녀왔다. 줍줍한 낙엽이 예쁘네.",
                imageUrl = "https://lh3.googleusercontent.com/proxy/DNVIwWacFoW3Za-pUNm8BiFDjLDOUAaq6y3dVk0TVXZSvlRvLGAqznzidRc1c7d-TqVhTxP8-h2D14HNgDEwfWvD0td6hQK1okNte93oCTs", // 노을 사진 예시
                location = NoteLocation(address = "서울시 성동구")
            ),
            // 2. 텍스트 위주의 메모 (이미지 예시 좌측 중앙)
            Note(
                noteId = "s2",
                userId = userId,
                userNickname = "줍줍이_92",
                contentText = "\"작은 것들로부터 얻는 커다란 행복\"",
                category = "MEMO", // 카테고리 활용
                imageUrl = null
            ),
            // 3. 사진과 긴 텍스트가 섞인 쪽지 (이미지 예시 중앙 하단)
            Note(
                noteId = "s3",
                userId = userId,
                userNickname = "줍줍이_92",
                contentText = "퇴근길에 본 하늘은 보라색이었다. 잊지 않으려고 기록한다.",
                imageUrl = "https://cdn.eyesmag.com/content/uploads/posts/2025/01/22/shutterstock_2491179401-06f50759-c2c5-49cb-b10b-ba47ca6d2166.jpg"
            ),
            // 4. 짧은 텍스트 메모
            Note(
                noteId = "s4",
                userId = userId,
                userNickname = "줍줍이_92",
                contentText = "플레이리스트 공유해용!",
                category = "MUSIC",
                imageUrl = null
            )
        )

        return Result.success(dummyScraps)
    }
}