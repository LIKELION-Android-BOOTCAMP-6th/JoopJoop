package com.example.joopjoop.feature.note.data.repository

import com.example.joopjoop.feature.note.data.model.NoteDTO
import com.example.joopjoop.feature.note.data.model.NoteRequest
import com.example.joopjoop.feature.note.data.source.FirestoreNoteSource

class NoteRepositoryImpl : NoteRepository {
    private val source = FirestoreNoteSource()

    override suspend fun getNotes(): List<NoteDTO> {
        return source.getNotes()
    }

    override suspend fun getNoteDetail(noteId: String): NoteDTO {
        return source.getNoteDetail(noteId)
    }

    override suspend fun createNote(request: NoteRequest): String {
        return source.saveNote(request)
    }

    override suspend fun incrementViewCount(noteId: String) {
        source.incrementViewCount(noteId)
    }

    override suspend fun updateLikeCount(noteId: String, increment: Int) {
        source.updateLikeCount(noteId, increment)
    }
}


