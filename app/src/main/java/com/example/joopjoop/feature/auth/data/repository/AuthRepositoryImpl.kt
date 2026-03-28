package com.example.joopjoop.feature.auth.data.repository

import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.data.model.AuthResult
import com.example.joopjoop.feature.auth.data.model.UserResponse
import com.example.joopjoop.feature.auth.data.source.FirebaseAuthSource
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource

class AuthRepositoryImpl (
    private val authSource: FirebaseAuthSource, // 사용자 인증 데이터
    private val userSource: FirestoreUserSource, // 사용자 데이터
) : AuthRepository {

    override suspend fun isNicknameAvailable(nickname: String): Boolean {
        return userSource.isNicknameAvailable(nickname)
    }

    override suspend fun signUp(
        email: String,
        password: String,
        nickname: String
    ): AuthResult<UserResponse> { // 가입된 정보 반환
        return try {
            // firebase auth를 통해 계정 생성
            val uid: String = authSource.signUp(email, password)

            // 계정 생성 성공 시 Firestore에 사용자 정보 저장
            userSource.saveUser(uid, email, nickname)

            // 성공 시
            val user = UserResponse(
                uid = uid,
                email = email,
                nickname = nickname
            )
            AuthResult.Success(user)
        } catch (e: Exception) {
            // 실패 시 에러 던지기
            AuthResult.Failure(e)
        }
    }

    // 로그인 성공 시 유저 정보를 가져와 반환
    override suspend fun login(
        email: String,
        password: String
    ): AuthResult<UserResponse> {
        return try {
            // FirebaseAuthSource를 통해 실제 로그인을 시도합니다.
            val uid = authSource.login(email, password)

            // 성공하면 Result.success를 반환합니다.
            AuthResult.Success(UserResponse(uid = uid, email = email))
        } catch (e: Exception) {
            // 실패(비번 틀림, 없는 계정 등)하면 에러와 함께 failure를 반환합니다.
            AuthResult.Failure(e)
        }
    }
    override suspend fun logout(): Result<Unit> {
        return try {
            // Firebase 로그아웃 수행
            authSource.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}