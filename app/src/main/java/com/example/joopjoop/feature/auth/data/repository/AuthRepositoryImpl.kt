package com.example.joopjoop.feature.auth.data.repository

import com.example.joopjoop.core.model.User
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.data.model.AuthResult
import com.example.joopjoop.feature.auth.data.model.UserResponse
import com.example.joopjoop.feature.auth.data.source.FirebaseAuthSource
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthRepositoryImpl(
    private val authSource: FirebaseAuthSource, // 사용자 인증 데이터
    private val userSource: FirestoreUserSource, // 사용자 데이터
    // 필요 시 외부 스코프를 주입받거나 내부에서 정의 (여기서는 단순화를 위해 GlobalScope 대신 내부 스코프 활용)
    externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : AuthRepository {

    // 유저 정보 캐시 (StateFlow)
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    init {
        // [자동 로그인] 앱 시작 시 로그인 상태 확인
        val savedUid = authSource.getCurrentUserId()
        if (savedUid != null) {
            externalScope.launch {
                val user = userSource.getUser(savedUid)
                _currentUser.value = user
            }
        }
    }

    // 프로필 업데이트 (닉네임, 프로필 이미지)
    override suspend fun updateProfile(newNickname: String, newImageUrl: String?): AuthResult<Unit> {
        return try {
            val uid = authSource.getCurrentUserId() ?: throw Exception("로그인 정보 없음")

            // 1. Firestore 유저 문서의 nickname 필드 업데이트
            userSource.updateUser(uid, newNickname)

            // 2. [핵심] 캐시(StateFlow) 업데이트
            // 현재 캐시된 유저 정보를 복사해서 닉네임만 갈아끼움
            _currentUser.value = _currentUser.value?.copy(nickname = newNickname)

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Failure(e)
        }
    }

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

            // [추가] 가입 성공 후 Firestore에서 유저 정보를 가져와 캐시 업데이트
            val fullUserInfo = userSource.getUser(uid)
            _currentUser.value = fullUserInfo

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

            // [추가] 로그인 성공 시 Firestore에서 유저 정보를 가져와 캐시 업데이트
            val fullUserInfo = userSource.getUser(uid)
            _currentUser.value = fullUserInfo

            // 성공하면 Result.success를 반환합니다.
            AuthResult.Success(UserResponse(uid = uid, email = email))
        } catch (e: Exception) {
            // 실패(비번 틀림, 없는 계정 등)하면 에러와 함께 failure를 반환합니다.
            AuthResult.Failure(e)
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        return try {
            // Firebase 로그아웃 수행
            authSource.logout()
            // [추가] 로그아웃 시 캐시 비우기
            _currentUser.value = null
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Failure(e)
        }
    }
}