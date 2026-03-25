package com.example.joopjoop.feature.note.data.source

import com.example.joopjoop.feature.note.data.model.NoteDTO
import com.example.joopjoop.feature.note.data.model.NoteRequest

// FirestoreNoteSource.kt 빈 틀만
class FirestoreNoteSource {
    suspend fun getNotes(): List<NoteDTO> { TODO() }
    suspend fun getNoteDetail(noteId: String): NoteDTO { TODO() }
    suspend fun createNote(request: NoteRequest) { TODO() }
}