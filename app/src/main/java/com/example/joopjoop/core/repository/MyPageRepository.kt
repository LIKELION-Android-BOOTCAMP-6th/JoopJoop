package com.example.joopjoop.core.repository

import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.Scrap
import com.example.joopjoop.core.model.User

interface MyPageRepository {
    // F-MY-01: 내 프로필 정보 가져오기
    suspend fun getUserProfile(userId: String): Result<User>

    // F-MY-02: 내가 쓴 쪽지 목록 가져오기
    suspend fun getMyPosts(userId: String): Result<List<Note>>

    // F-MY-03: 내가 스크랩한 쪽지 목록 가져오기
    suspend fun getMyScraps(userId: String): Result<List<Scrap>>
}