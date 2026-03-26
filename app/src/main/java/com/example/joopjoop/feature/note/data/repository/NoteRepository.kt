package com.example.joopjoop.feature.note.data.repository

import com.example.joopjoop.feature.note.data.model.NoteDTO
import com.example.joopjoop.feature.note.data.model.NoteRequest

interface NoteRepository {
    suspend fun getNotes(): List<NoteDTO>
    suspend fun getNoteDetail(noteId: String): NoteDTO
    suspend fun createNote(request: NoteRequest): String
    suspend fun incrementViewCount(noteId: String)
    suspend fun updateLikeCount(noteId: String, increment: Int)

}