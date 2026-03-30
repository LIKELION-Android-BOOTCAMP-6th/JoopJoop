package com.example.joopjoop.feature.mypage.data.repository

import android.util.Log
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.User
import com.example.joopjoop.core.repository.MyPageRepository
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MyPageRepositoryImpl(
    private val userSource: FirestoreUserSource,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : MyPageRepository {

    // F-MY-01: 내 프로필 정보 가져오기
    override suspend fun getUserProfile(userId: String): Result<User> = runCatching {
        userSource.getUser(userId) ?: throw Exception("존재하지 않는 사용자입니다.")
    }

    // F-MY-02: 내가 쓴 쪽지 목록 가져오기
    override suspend fun getMyPosts(userId: String): Result<List<Note>> = runCatching {
        try{
            Log.d("MyPageRepo", "조회 시작 - 내 UID: $userId")

        val snapshot = firestore.collection("notes")
            .whereEqualTo("authorId", userId)
            .get()
            .await()

            val notes = snapshot.toObjects(Note::class.java)
            Log.d("MyPageRepo", "조회 성공 - 가져온 개수: ${notes.size}")
            notes
        } catch (e: Exception) {
            // 에러의 원인 로그
            Log.e("MyPageRepo", "Firestore 에러 발생: ${e.message}")
            Log.e("MyPageRepo", "에러 상세 원인: ", e)
            throw e // runCatching이 에러를 잡아서 Result.failure로 
        }
    }

    // F-MY-03: 내가 스크랩한 쪽지 목록 가져오기
    override suspend fun getMyScraps(userId: String): Result<List<Note>> = runCatching {
        // 1. 유저 하위 컬렉션 'scraps'에서 모든 문서(스크랩 정보)를 가져옵니다.
        val scrapSnapshot = firestore.collection("users")
            .document(userId)
            .collection("scraps")
            .get()
            .await()

        // 2. Scrap 모델에서 noteId 리스트만 추출합니다.
        val scrapIds = scrapSnapshot.documents.map { it.id } // 문서 ID가 곧 noteId인 경우

        if (scrapIds.isEmpty()) return@runCatching emptyList<Note>()

        // 3. 추출한 noteIds를 이용해 실제 'notes' 컬렉션에서 상세 정보를 가져옵니다.
        val notesSnapshot = firestore.collection("notes")
            .whereIn(FieldPath.documentId(), scrapIds)
            .get()
            .await()

        notesSnapshot.toObjects(Note::class.java)
    }
}