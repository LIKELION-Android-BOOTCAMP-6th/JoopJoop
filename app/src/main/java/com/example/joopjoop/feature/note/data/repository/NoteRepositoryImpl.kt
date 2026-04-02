package com.example.joopjoop.feature.note.data.repository

import android.util.Log
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.Scrap
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.note.data.source.FirestoreNoteSource
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okio.`-DeprecatedOkio`.source

class NoteRepositoryImpl(
    private val source: FirestoreNoteSource,
) : NoteRepository {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    override suspend fun uploadImage(
        originalData: ByteArray,
        thumbnailData: ByteArray,
        fileName: String,
        onProgress: (Float) -> Unit
    ): Pair<String, String>? {
        return try {
            val originalRef = storage.reference.child("notes/images/$fileName.jpg")
            val thumbnailRef = storage.reference.child("notes/thumbnails/${fileName}_thumb.jpg")

            // 원본과 썸네일 동시 업로드 - IO 스레드에서 수행
            val uploadResult = withContext(Dispatchers.IO) {
                coroutineScope {
                    val originalDeferred = async {
                        originalRef.putBytes(originalData)
                            .addOnProgressListener { taskSnapshot ->
                                val progress = (taskSnapshot.bytesTransferred.toDouble() /
                                        taskSnapshot.totalByteCount.toDouble()).toFloat()
                                onProgress(progress)
                            }
                            .await()
                        originalRef.downloadUrl.await().toString()
                    }

                    val thumbnailDeferred = async {
                        thumbnailRef.putBytes(thumbnailData).await()
                        thumbnailRef.downloadUrl.await().toString()
                    }

                    Pair(originalDeferred.await(), thumbnailDeferred.await())
                }
            }

        // 결과 반환 (Main 스레드로 복귀)
        uploadResult

    } catch (e: Exception) {
        Log.e("PhotoDebug", "업로드 중 에러: ${e.message}")
        null
    }
}

    // 반환 타입을 Pair<String, String>? 로 변경 (원본URL, 썸네일URL)
//    override suspend fun uploadImage(
//        originalData: ByteArray,    // 원본 데이터
//        thumbnailData: ByteArray,   // 썸네일 데이터
//        fileName: String,
//        onProgress: (Float) -> Unit
//    ): Pair<String, String>? { // 두 개의 URL을 반환하도록 변경
//        return try {
//            // 경로를 각각 다르게 설정 (폴더 분리)
//            val originalRef = storage.reference.child("notes/images/$fileName.jpg")
//            val thumbnailRef = storage.reference.child("notes/thumbnails/${fileName}_thumb.jpg")
//
//            // 원본과 썸네일 동시 업로드
//            val originalDeferred = CoroutineScope(Dispatchers.IO).async {
//                originalRef.putBytes(originalData)
//                    .addOnProgressListener { taskSnapshot ->
//                        val progress = (taskSnapshot.bytesTransferred.toDouble() /
//                                taskSnapshot.totalByteCount.toDouble()).toFloat()
//                        onProgress(progress)
//                    }
//                    .await()
//                originalRef.downloadUrl.await().toString()
//            }
//
//            val thumbnailDeferred = CoroutineScope(Dispatchers.IO).async {
//                thumbnailRef.putBytes(thumbnailData).await()
//                thumbnailRef.downloadUrl.await().toString()
//            }
//
//            /*// 1. 원본 업로드 (진행률은 원본 기준으로 표시)
//            val originalTask = originalRef.putBytes(originalData)
//            originalTask.addOnProgressListener { taskSnapshot ->
//                val progress =
//                    (taskSnapshot.bytesTransferred.toDouble() / taskSnapshot.totalByteCount.toDouble()).toFloat()
//                onProgress(progress)
//            }.await()
//            val originalUrl = originalRef.downloadUrl.await().toString()
//
//            // 2. 썸네일 업로드
//            thumbnailRef.putBytes(thumbnailData).await()
//            val thumbnailUrl = thumbnailRef.downloadUrl.await().toString()
//*/
//            val originalUrl = originalDeferred.await()
//            val thumbnailUrl = thumbnailDeferred.await()
//
//            // 두 URL을 묶어서 반환
//            Pair(originalUrl, thumbnailUrl)
//
//        } catch (e: Exception) {
//            Log.e("PhotoDebug", "업로드 중 에러: ${e.message}")
//            null
//        }
//    }

    // 함수명을 변경했습니다.
    // 사용자 위치 중심으로 주변 쪽지 쿼리 ( 내 쪽지 포함 )
    override suspend fun getNotesByLocation(
        lat: Double,
        lng: Double,
        myUid: String
    ): List<Note> {
        return source.getNotesByLocation(lat, lng)
    }

    // 쪽지 상세 데이터 가져오기
    override suspend fun getNoteDetail(noteId: String): Note {
        return source.getNoteDetail(noteId)
    }

    // 쪽지 만들기
    override suspend fun createNote(request: Note) {
        source.createNote(request)
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
    override suspend fun addLike(noteId: String, userId: String) {
        source.addLike(noteId, userId)
    }

    // 좋아요 취소
    override suspend fun removeLike(noteId: String, userId: String) {
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

    // 쪽지 삭제
    override suspend fun deleteNote(noteId: String): Boolean {
        return source.deleteNote(noteId)
    }

    // 수정한 쪽지 제출
    override suspend fun submitEditedNote(noteId: String, updatedNote: Note) {
        source.submitEditedNote(noteId, updatedNote)
    }
}

