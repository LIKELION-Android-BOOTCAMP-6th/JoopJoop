package com.example.joopjoop.core.repository

import com.example.joopjoop.core.model.Note
import com.example.joopjoop.feature.note.data.model.NoteRequest

interface NoteRepository {
    suspend fun getNotes(): List<Note>
    suspend fun getNoteDetail(noteId: String): Note
    suspend fun createNote(request: NoteRequest): String
    suspend fun incrementViewCount(noteId: String)
    suspend fun updateLikeCount(noteId: String, increment: Int)

}