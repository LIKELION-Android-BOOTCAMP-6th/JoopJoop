package com.example.joopjoop.feature.note.data.repository

import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.note.data.model.NoteDTO
import com.example.joopjoop.feature.note.data.model.NoteRequest
import com.example.joopjoop.feature.note.data.source.FirestoreNoteSource

class NoteRepositoryImpl(
    private val source: FirestoreNoteSource
) : NoteRepository {

    override suspend fun getNotes(): List<NoteDTO> {
        return source.getNotes()
    }

    override suspend fun getNoteDetail(noteId: String): NoteDTO {
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


