package com.example.joopjoop.feature.note.data.repository

import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.model.Note
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

    override suspend fun getNoteDetail(noteId: String): Note {
        return source.getNoteDetail(noteId)
    }

    override suspend fun createNote(request: NoteRequest): String {
        val noteData = hashMapOf(
            "content" to request.content,
            "category" to request.category,
            "storageHours" to request.storageHours,
            "imageUri" to request.imageUri,
            "latitude" to request.latitude,
            "longitude" to request.longitude,
            "createdAt" to System.currentTimeMillis(),
            "authorName" to request.authorName,
            "location" to request.location
        )
        return source.createNote(request)
    }

    override suspend fun incrementViewCount(noteId: String) {
        source.incrementViewCount(noteId)
    }

    override suspend fun updateLikeCount(noteId: String, increment: Int) {
        source.updateLikeCount(noteId, increment)
    }

}


