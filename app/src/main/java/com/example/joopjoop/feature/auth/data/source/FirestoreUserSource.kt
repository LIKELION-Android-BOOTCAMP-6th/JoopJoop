package com.example.joopjoop.feature.auth.data.source

import com.example.joopjoop.core.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreUserSource(
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val userCollection = db.collection("users")

    // 닉네임 중복 확인
    suspend fun isNicknameAvailable(nickname: String): Boolean {
        return try {
            val snapshot = userCollection
                .whereEqualTo("nickname", nickname)
                .get()
                .await()
            snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    // 회원가입 성공 시 유저 정보 저장
    suspend fun saveUser(uid: String, email: String, nickname: String) {
        val userMap = hashMapOf(
            "uid" to uid, // 유저 UID
            "email" to email, // 유저 이메일
            "nickname" to nickname, // 유저 닉네임
            "createAt" to System.currentTimeMillis() // 가입 시간 저장
        )

        // Auth에서 받은 UID를 문서 ID로 사용하여 저장
        // UID 가지고 유저의 정보 찾기 가능
        userCollection.document(uid).set(userMap).await()

    }

    // UID로 유저 정보 가져오기
    suspend fun getUser(uid: String): User? {
        return try {
            val snapshot = userCollection.document(uid).get().await()
            if (snapshot.exists()) {
                // Firestore 문서를 User 데이터 클래스로 자동 매핑
                snapshot.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}