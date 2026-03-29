package com.example.joopjoop.feature.note.data.repository

import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.Scrap
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.note.data.model.NoteRequest
import com.example.joopjoop.feature.note.data.source.FirestoreNoteSource

class NoteRepositoryImpl(
    private val source: FirestoreNoteSource
) : NoteRepository {

    // 이건 fireStore에서 모든 쪽지를 긁어오는 것처럼 보입니다.
    // 아래에 getNotesByLocation 함수를 새로 만들겠습니다
//    override suspend fun getNotes(): List<Note> {
//        return source.getNotes()
//    }

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


