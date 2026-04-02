package com.example.joopjoop.feature.auth.data.repository

import android.util.Log
import com.example.joopjoop.core.model.User
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.data.model.AuthResult
import com.example.joopjoop.feature.auth.data.model.UserResponse
import com.example.joopjoop.feature.auth.data.source.FirebaseAuthSource
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource
import com.example.joopjoop.feature.note.data.source.FirestoreNoteSource
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.joopjoop.feature.note.data.source.FirestoreNoteSource // [추가] 탈퇴 시 notes 비활성화용

class AuthRepositoryImpl(
    private val authSource: FirebaseAuthSource, // 사용자 인증 데이터
    private val userSource: FirestoreUserSource, // 사용자 데이터
    private val noteSource: FirestoreNoteSource,
    // 필요 시 외부 스코프를 주입받거나 내부에서 정의 (여기서는 단순화를 위해 GlobalScope 대신 내부 스코프 활용)
    private val noteSource: FirestoreNoteSource, // [추가] 탈퇴 시 작성한 notes 비활성화
    externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : AuthRepository {

    override fun getCurrentUid(): String? {
        return authSource.getCurrentUserId() // 이미 존재하는 authSource의 기능을 활용합니다.
    }

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    init {
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

            // Firestore 유저 문서의 nickname 필드 업데이트
            userSource.updateUser(uid, newNickname, newImageUrl)

            // 캐시(StateFlow) 업데이트
            // 현재 캐시된 유저 정보를 복사해서 닉네임만 갈아끼움
            _currentUser.value = _currentUser.value?.copy(
                nickname = newNickname,
                profileImageUrl = newImageUrl ?: ""
            )

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Failure(e)
        }
    }

    override suspend fun isNicknameAvailable(nickname: String): Boolean {
        return userSource.isNicknameAvailable(nickname)
    }

    override suspend fun uploadProfileImage(imageBytes: ByteArray): AuthResult<String> {
        return try {
            val uid = getCurrentUid() ?: return AuthResult.Failure(Exception("로그인 정보가 없습니다."))

            // 1. Storage 참조 생성 (경로: profiles/유저ID.jpg)
            val storageRef = Firebase.storage.reference.child("profiles/$uid.jpg")

            // 2. ByteArray 데이터 업로드
            storageRef.putBytes(imageBytes).await()

            // 3. 업로드된 파일의 공개 URL 가져오기
            val downloadUrl = storageRef.downloadUrl.await().toString()

            AuthResult.Success(downloadUrl)
        } catch (e: Exception) {
            AuthResult.Failure(e)
        }
    }

    // 프로필 사진 삭제, 기본 이미지로
    override suspend fun deleteProfileImage(): AuthResult<Unit> {
        return try {
            val user = _currentUser.value ?: return AuthResult.Failure(Exception("로그인 정보 없음"))
            val uid = user.uid
            val currentNickname = user.nickname // [중요] 현재 닉네임 가져오기

            // 1. Storage에서 사진 파일 삭제
            val storageRef = Firebase.storage.reference.child("profiles/$uid.jpg")
            try {
                storageRef.delete().await()
            } catch (e: Exception) {
                // 이미 삭제되었거나 없는 경우를 위해 예외 처리 (무시하고 진행)
            }

            // 2. Firestore 업데이트: 닉네임은 유지하고 이미지만 null로 변경
            userSource.updateUser(uid, currentNickname, null)

            // 3. 앱 내 상태(State) 즉시 반영
            _currentUser.value = user.copy(profileImageUrl = null)

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Failure(e)
        }
    }

    override suspend fun getUserInfoByUid(uid: String): User? {
        return try {
            // 이미 초기화 시점에 사용하던 userSource.getUser를 그대로 활용
            userSource.getUser(uid)
        } catch (e: Exception) {
            null
        }
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

    // 사용자가 쓴 쪽지 수
    override suspend fun getUserNoteCount(uid: String): Int {
        // 직접 쿼리하지 않고 전문가(NoteSource)에게 물어봅니다.
        return noteSource.getUserNoteCount(uid)
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
    // [추가] 회원 탈퇴
    override suspend fun withdraw(): AuthResult<Unit> {
        return try {
            val uid = authSource.getCurrentUserId()
                ?: throw Exception("로그인 정보 없음")

            userSource.deactivateUser(uid)
            noteSource.deactivateUserNotes(uid)
            authSource.deleteUser()

            _currentUser.value = null

            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Failure(e)
        }
    }
}