package com.example.joopjoop.core.repository

import com.example.joopjoop.core.model.User
import com.example.joopjoop.feature.auth.data.model.AuthResult
import com.example.joopjoop.feature.auth.data.model.UserResponse
import kotlinx.coroutines.flow.Flow

// 인증 및 사용자 관리
interface AuthRepository {

    // 실시간 유저 상태 관찰 (캐시)
    val currentUser: Flow<User?>

    /**
     * 사용자 프로필 정보 업데이트
     * [F-MY-04] 닉네임 변경 대응
     */
    suspend fun updateProfile(
        newNickname: String,
        newImageUrl: String? = null
    ): AuthResult<Unit>

    // 닉네임 중복 확인
    // nickname 은 검사할 닉네임
    // 사용 가능 = true, 사용불가능 = false
    suspend fun isNicknameAvailable(nickname: String): Boolean

    // 이메일 회원가입 실행
    // email은 사용자 이메일
    // password는 사용자 비밀번호
    // nickname은 사용자 닉네임
    // return은 성공 시 Result.Success, 실패 시 에러내용 + Result.Failure
    suspend fun signUp(
        email: String,
        password: String,
        nickname: String
    ): AuthResult<UserResponse>

    // 이메일 로그인 실행
    // return: 성공 시 AuthResult.Success(UserResponse)
    // 실패 시 AuthResult.Failure(Exception)
    suspend fun login(
        email: String,
        password: String
    ): AuthResult<UserResponse>

    //로그아웃
    suspend fun logout(): AuthResult<Unit>
}