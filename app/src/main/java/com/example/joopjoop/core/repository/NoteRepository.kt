package com.example.joopjoop.core.repository

import com.example.joopjoop.core.model.Note
import com.example.joopjoop.feature.note.data.model.NoteRequest

interface NoteRepository {

    // 이건 fireStore에서 모든 쪽지를 긁어오는 것처럼 보입니다.
    // 아래에 getNotesByLocation 함수를 새로 만들겠습니다
//    suspend fun getNotes(): List<Note>

    //주변 쪽지 검색
    suspend fun getNotesByLocation(lat: Double, lng: Double): List<Note>
    suspend fun getNoteDetail(noteId: String): Note? // null을 허용하도록 수정
    suspend fun createNote(request: NoteRequest): String
    suspend fun incrementViewCount(noteId: String)
    suspend fun updateLikeCount(noteId: String, increment: Int)

}