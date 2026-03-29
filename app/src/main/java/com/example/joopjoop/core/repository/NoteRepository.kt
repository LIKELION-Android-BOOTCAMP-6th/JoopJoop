package com.example.joopjoop.core.repository

import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.Scrap
import com.example.joopjoop.feature.note.data.model.NoteRequest

interface NoteRepository {

    // 이건 fireStore에서 모든 쪽지를 긁어오는 것처럼 보입니다.
    // 아래에 getNotesByLocation 함수를 새로 만들겠습니다
//    suspend fun getNotes(): List<Note>

    //주변 쪽지 검색
    suspend fun getNotesByLocation(lat: Double, lng: Double): List<Note>

    // 쪽지 상세 데이터 가져오기
    suspend fun getNoteDetail(noteId: String): Note? // null을 허용하도록 수정

    // 쪽지 만들기
    suspend fun createNote(request: NoteRequest): String

    // 조회수 증가
    suspend fun incrementViewCount(noteId: String)

    // 좋아요 업데이트
    suspend fun updateLikeCount(noteId: String, increment: Int)

    // 스크랩 하기
    suspend fun saveScrapNote(scrap: Scrap, userId: String)

    // 스크랩 삭제
    suspend fun removeBookmark(noteId: String, userId: String)

    // 쪽지 스크랩 조회
    suspend fun isNoteBookmarked(noteId: String, userId: String): Boolean
}