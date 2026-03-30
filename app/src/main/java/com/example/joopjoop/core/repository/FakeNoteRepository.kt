package com.example.joopjoop.core.repository

import android.util.Log
import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import com.example.joopjoop.core.model.Scrap
import com.example.joopjoop.feature.note.data.model.NoteRequest
import com.example.joopjoop.feature.note.data.source.FirestoreNoteSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.util.Date

class FakeNoteRepository : NoteRepository {
    companion object {
        // 앱이 켜져 있는 동안 데이터를 유지하는 가짜 DB
        private val _allFakeNotes = mutableListOf<Note>()
        private var isInitialized = false
    }

    override suspend fun getNotesByLocation(lat: Double, lng: Double): List<Note> {
        delay(500)
        // 초기 1회 현재 위치 기반 샘플 생성
        if (!isInitialized) {
            _allFakeNotes.addAll(FakeDataSource.getFakeNotes(lat, lng))
            isInitialized = true
        }

        // Firestore와 동일하게 Geohash 앞 5자리로 주변 노트를 필터링
        val centerGeohash = LocationUtil.getGeohash(lat, lng).take(5)
        return _allFakeNotes.filter { it.location.geohash.startsWith(centerGeohash) }
    }
    private val firestoreSource = FirestoreNoteSource()
    override suspend fun getNoteDetail(noteId: String): Note? {
//        return _allFakeNotes.find { it.noteId == noteId }
            return try {
                Log.d("UIDebug", "Repository: Firestore에 데이터 요청 중... ID: $noteId")

                // 🌟 진짜 Firestore 소스 호출
                val remoteNote = firestoreSource.getNoteDetail(noteId)

                Log.d("UIDebug", "Repository: 서버에서 읽어온 숫자 = ${remoteNote.likeCount}")

                // 인터페이스가 Note? 를 원하므로 remoteNote(Note)를 그대로 반환해도 됩니다.
                remoteNote
            } catch (e: Exception) {
                Log.e("UIDebug", "Repository 에러 발생: ${e.message}")
                // 에러 시 null을 줘서 앱이 죽지 않게 방어합니다.
                null
            }
        }

    override suspend fun createNote(request: NoteRequest): String {
        delay(800)
        val newId = "user_note_${System.currentTimeMillis()}"

        // 작성한 노트를 실제 리스트에 추가하여 다른 화면에서도 보이게 함
        val newNote = Note(
            noteId = newId,
            userId = "me",
            userNickname = request.authorName ?: "나",
            userProfileImageUrl = "",
            contentText = request.content,
            imageUrl = request.imageUri,
            category = request.category,
            viewCount = 0,
            likeCount = 0,
            location = NoteLocation(
                geohash = LocationUtil.getGeohash(request.latitude, request.longitude),
                latitude = request.latitude,
                longitude = request.longitude,
                address = request.location ?: "작성 위치",
                distance = ""
            ),
            isActive = true,
            createdAt = Date(),
            expiresAt = Date(System.currentTimeMillis() + (request.storageHours * 3600000L))
        )
        // 리스트 맨 앞에 추가하여 최신순 유지
        _allFakeNotes.add(0, newNote)
        return newId
    }

    override suspend fun incrementViewCount(noteId: String) {
        val index = _allFakeNotes.indexOfFirst { it.noteId == noteId }
        if (index != -1) {
            _allFakeNotes[index] =
                _allFakeNotes[index].copy(viewCount = _allFakeNotes[index].viewCount + 1)
        }
    }

    override suspend fun updateLikeCount(noteId: String, increment: Int) {
        val index = _allFakeNotes.indexOfFirst { it.noteId == noteId }
        if (index != -1) {
            _allFakeNotes[index] =
                _allFakeNotes[index].copy(likeCount = _allFakeNotes[index].likeCount + increment)
        }
    }

    override suspend fun addLike(noteId: String, userId: String) {
        firestoreSource.addLike(noteId, userId)
    }

    override suspend fun removeLike(noteId: String, userId: String) {
        firestoreSource.removeLike(noteId, userId)
    }

    override suspend fun checkLikeExists(noteId: String, userId: String): Boolean {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        return try {
            val document = db.collection("users")
                .document(userId)
                .collection("likes")
                .document(noteId)
                .get()
                .await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun saveScrapNote(scrap: Scrap, userId: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .collection("scraps")
            .document(scrap.noteId) // 중복 스크랩 방지
            .set(scrap)
            .await()
    }

    override suspend fun removeBookmark(noteId: String, userId: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .collection("scraps")
            .document(noteId)
            .delete()
            .await()
    }

    override suspend fun isNoteBookmarked(noteId: String, userId: String): Boolean {
        return try {
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val document = db.collection("users")
                .document(userId)
                .collection("scraps")
                .document(noteId)
                .get()
                .await()
            document.exists() // 문서가 있으면 true, 없으면 false
        } catch (e: Exception) {
            false
        }
    }

    // 쪽지 수정
    override suspend fun editNote(noteId: String, request: NoteRequest) {
        // todo :: 수정 로직 추가
    }

    // 쪽지 삭제
    override suspend fun deleteNote(noteId: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("notes").document(noteId).delete().await()
    }
}


//import com.example.joopjoop.core.common.util.LocationUtil
//import com.example.joopjoop.core.model.Note
//import com.example.joopjoop.feature.note.data.model.NoteRequest
//import kotlinx.coroutines.delay
//
//class FakeNoteRepository : NoteRepository {
//    // 메모리 내에서 관리할 가짜 데이터 리스트
//    private var _fakeNotes = mutableListOf<Note>()
//
//    /**
//     * 초기 데이터를 생성하는 함수 (DI 컨테이너나 ViewModel에서 초기화 시 호출)
//     * @param lat 기준 위도 (사용자 위치 등)
//     * @param lng 기준 경도 (사용자 위치 등)
//     */
//
////    override suspend fun getNotes(): List<Note> {
////        delay(500) // 실제 네트워크 통신 느낌을 주기 위한 딜레이
////        return _fakeNotes
////    }
//
//    override suspend fun getNotesByLocation(
//        lat: Double,
//        lng: Double
//    ): List<Note> {
//        // 1. 실제 네트워크 통신 느낌을 주기 위한 지연 시간 (테스트용)
//        delay(500)
//
//        // 2. [Geohash 계산]
//        // 실제 서버 쿼리 시에는 이 hash값을 startAt, endAt에 쓰겠지만,
//        // Fake에서는 데이터가 실제 위치 기반으로 생성되었음을 보장하는 용도로 씁니다.
//        val currentGeohash = LocationUtil.getGeohash(lat, lng)
//        println("Log: 현재 위치 Geohash 계산됨 -> $currentGeohash")
//
//        // 3. [범위의 후보 쪽지 조회/생성]
//        // FakeDataSource를 호출하여 현재 좌표(lat, lng) 기준
//        // '줍기 가능 거리(30m)'와 '조회 가능 거리(5km)'에 맞는 가짜 쪽지 11개를 만듭니다.
//        val fakeNotes = FakeDataSource.getFakeNotes(lat, lng)
//
//        // 4. 생성된 데이터를 내부 리스트에 업데이트 (상세 보기 등 후속 작업을 위해 저장)
//        _fakeNotes.clear()
//        _fakeNotes.addAll(fakeNotes)
//
//        return _fakeNotes
//    }
//
//    override suspend fun getNoteDetail(noteId: String): Note? {
//        delay(300)
//        // noteId 필드명에 맞춰서 검색합니다.
//        return _fakeNotes.find { it.noteId == noteId }
//    }
//
//    override suspend fun createNote(request: NoteRequest): String {
//        delay(1000)
//        return "new_note_${System.currentTimeMillis()}"
//    }
//
//    override suspend fun incrementViewCount(noteId: String) {
//        val index = _fakeNotes.indexOfFirst { it.noteId == noteId }
//        if (index != -1) {
//            val note = _fakeNotes[index]
//            _fakeNotes[index] = note.copy(viewCount = note.viewCount + 1)
//        }
//    }
//
//    override suspend fun updateLikeCount(noteId: String, increment: Int) {
//        val index = _fakeNotes.indexOfFirst { it.noteId == noteId }
//        if (index != -1) {
//            val note = _fakeNotes[index]
//            _fakeNotes[index] = note.copy(likeCount = note.likeCount + increment)
//        }
//    }
//}