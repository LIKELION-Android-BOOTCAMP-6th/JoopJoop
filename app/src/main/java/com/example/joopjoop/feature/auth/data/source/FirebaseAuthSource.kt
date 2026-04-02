package com.example.joopjoop.feature.auth.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

// Firebase Authentication 서비스와 직접 통신하는 데이터 소스
class FirebaseAuthSource {

    // Firebase Auth 인스턴스 초기화
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 현재 로그인된 사용자의 UID를 즉시 가져옴 (캐시 및 체크용)
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // 현재 로그인된 FirebaseUser 객체 자체를 가져옴
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // 새로운 계정 생성하고 생성된 사용자의 UID 반환
    // email은 사용자 이메일, password는 사용자 비밀번호, return은 생성된 사용자의 고유 UID
    suspend fun signUp(email: String, password: String): String {
        return try {
            // Firebase 서버에 계정 생성 요청
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            // 생성 성공 시 유저의 UID를 추출하여 반환
            authResult.user?.uid ?: throw Exception("계정 생성 실패")
        } catch (e: Exception) {
            // 이메일 형식이 틀렸거나 이미 가입된 경우 에러 발생
            // 에러 메세지는 viewmodel의 Result.failure로 전달
            throw e
        }
    }

    // 로그인
    suspend fun login(email: String, password: String): String { // : String 추가
        val result = auth.signInWithEmailAndPassword(email, password).await()
        // 로그인된 유저의 UID를 반환합니다.
        return result.user?.uid ?: throw Exception("로그인 실패")
    }

    fun logout() {
        auth.signOut()
    }

suspend fun deleteUser() {
    val currentUser = auth.currentUser ?: throw Exception("로그인된 사용자가 없습니다.")
    currentUser.delete().await()
}
}