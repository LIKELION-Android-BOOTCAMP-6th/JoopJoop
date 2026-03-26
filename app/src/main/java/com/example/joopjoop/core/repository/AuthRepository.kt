package com.example.joopjoop.core.repository

// 인증 및 사용자 관리
interface AuthRepository{

    // 닉네임 중복 확인
    // nickname 은 검사할 닉네임
    // 사용 가능 = true, 사용불가능 = false
    suspend fun isNicknameAvailable(nickname: String): Boolean

    // 이메일 회원가입 실행
    // email은 사용자 이메일
    // password는 사용자 비밀번호
    // nickname은 사용자 닉네임
    // return은 성공 시 Result.Success, 실패 시 에러내용 + Result.Failure
    suspend fun signUp(email: String,
                       password: String,
                       nickname: String
    ): Result<Unit>

    suspend fun login(email: String,
                      password: String
    ): Result<Unit>
}