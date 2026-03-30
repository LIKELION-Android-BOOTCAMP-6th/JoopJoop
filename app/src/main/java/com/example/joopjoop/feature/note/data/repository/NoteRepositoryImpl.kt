package com.example.joopjoop.feature.note.data.repository

import android.net.Uri
import com.example.joopjoop.core.common.util.ImageProcessor
import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.Scrap
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.note.data.model.NoteRequest
import com.example.joopjoop.feature.note.data.source.FirestoreNoteSource
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class NoteRepositoryImpl(
    private val source: FirestoreNoteSource,
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
) : NoteRepository {

    // 이건 fireStore에서 모든 쪽지를 긁어오는 것처럼 보입니다.
    // 아래에 getNotesByLocation 함수를 새로 만들겠습니다
//    override suspend fun getNotes(): List<Note> {
//        return source.getNotes()
//    }
        override suspend fun uploadImage(processedData: ByteArray, fileName: String): String? {
            return try {
                val storageRef = storage.reference.child("notes/$fileName.jpg")

                // 1. 이미지 업로드 (ImageProcessor가 만든 ByteArray 사용)
                storageRef.putBytes(processedData).await()

                // 2. 업로드 완료 후 이미지의 '진짜 주소(URL)' 가져오기
                val downloadUrl = storageRef.downloadUrl.await()
                downloadUrl.toString() // 이 URL을 Firestore의 imageUri 필드에 저장하면 됩니다!
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    // 주변 쪽지 탐색
    override suspend fun getNotesByLocation(
        lat: Double,
        lng: Double
    ): List<Note> {
        // 1. Geohash 계산 (5자리)
        val centerGeohash = LocationUtil.getGeohash(lat, lng).take(5)

        // 2. Source에 구현된 위치 쿼리 호출
        return source.getNotesByLocation(centerGeohash)
    }

    // 쪽지 상세 데이터 가져오기
    override suspend fun getNoteDetail(noteId: String): Note {
        return source.getNoteDetail(noteId)
    }

    // 쪽지 만들기
    override suspend fun createNote(request: NoteRequest): String {
        val noteData = hashMapOf(
            "content" to request.content,
            "category" to request.category,
            "storageHours" to request.storageHours,
            "imageUri" to request.imageUri,
            "latitude" to request.latitude,
            "longitude" to request.longitude,
            "createdAt" to System.currentTimeMillis(),
            "authorId" to request.authorId,
            "authorName" to request.authorName,
            "location" to request.location,
            "geohash" to request.geohash
        )
        return source.createNote(request)
    }

    // 조회수 증가
    override suspend fun incrementViewCount(noteId: String) {
        source.incrementViewCount(noteId)
    }

    // 좋아요 업데이트
    override suspend fun updateLikeCount(noteId: String, increment: Int) {
        source.updateLikeCount(noteId, increment)
    }

    // 좋아요 추가
    override suspend fun addLike(noteId: String, userId: String){
        source.addLike(noteId, userId)
    }

    // 좋아요 취소
    override suspend fun removeLike(noteId: String, userId: String){
        source.removeLike(noteId, userId)
    }

    // 좋아요 여부 조회
    override suspend fun checkLikeExists(noteId: String, userId: String): Boolean {
        return source.checkLikeExists(noteId, userId)
    }

    // 스크랩 하기
    override suspend fun saveScrapNote(scrap: Scrap, userId: String) {
        source.saveScrapNote(scrap, userId)
    }

    // 스크랩 취소
    override suspend fun removeBookmark(noteId: String, userId: String) {
        source.cancelScrapNote(noteId, userId)
    }

    // 스크랩 상태 조회
    override suspend fun isNoteBookmarked(noteId: String, userId: String): Boolean {
        // 소스의 함수를 호출 (아래 2번에서 구현)
        return source.checkBookmarkExists(noteId, userId)
    }

    // 쪽지 수정
    override suspend fun editNote(noteId: String, request: NoteRequest) {
        // todo :: 쪽지 수정 로직 추가
    }

    // 쪽지 삭제
    override suspend fun deleteNote(noteId: String) {
        source.deleteNote(noteId)
    }

}


