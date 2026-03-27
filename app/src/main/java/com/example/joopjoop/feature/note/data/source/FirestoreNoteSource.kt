package com.example.joopjoop.feature.note.data.source

import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import com.example.joopjoop.feature.note.data.model.NoteRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FirestoreNoteSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collectionPath = "notes"

    fun formatDate(date: Date?): String {
        if (date == null) return ""
        val formatter = SimpleDateFormat("M월 dd일", Locale.getDefault())
        return formatter.format(date)
    }

    suspend fun getNotes(): List<Note> {
        val snapshot = db.collection(collectionPath).get().await()
        return snapshot.documents.mapNotNull { doc ->
            val timestamp = doc.getTimestamp("createdAt")
            Note(
                noteId = doc.id,
                userNickname = doc.getString("authorName") ?: "",
                createdAt = timestamp?.toDate() ?: Date(),
                contentText = doc.getString("content") ?: "",
//                distance = "0m"
            )
        }
    }

    suspend fun getNoteDetail(noteId: String): Note {
        val doc = db.collection(collectionPath).document(noteId).get().await()
        val timestamp = doc.getTimestamp("createdAt")
        return Note(
            noteId = doc.id,
            userNickname = doc.getString("authorName") ?: "익명 사용자",
            createdAt = timestamp?.toDate() ?: Date(),
            viewCount = doc.getLong("viewCount")?.toInt() ?: 0,
            likeCount = doc.getLong("likeCount")?.toInt() ?: 0,
            contentText = doc.getString("content") ?: "내용 없음",
            location = NoteLocation(
                geohash = doc.getString("geohash") ?: "",
                latitude = doc.getDouble("latitude") ?: 0.0,
                longitude = doc.getDouble("longitude") ?: 0.0,
                address = doc.getString("location") ?: "위치 정보 없음"
            )
        )
    }

    suspend fun createNote(request: NoteRequest): String {
        // 1. ID 자동 생성
        val documentRef = db.collection(collectionPath).document()
        val generatedId = documentRef.id

        // 2. 서버에 저장할 데이터 구성
        val noteData = hashMapOf(
            "id" to generatedId,
            "authorName" to request.authorName,
            "location" to request.location,
            "content" to request.content,
            "category" to request.category,
            "storageHours" to request.storageHours,
            "imageUri" to request.imageUri,
            "createdAt" to com.google.firebase.Timestamp.now(),
            "latitude" to request.latitude,
            "longitude" to request.longitude
        )


        // 3. Firestore에 저장 (await - 문서 저장 반환)
        documentRef.set(noteData).await()

        return generatedId // 생성된 ID 반환
    }

    suspend fun incrementViewCount(noteId: String) {
        val docRef = db.collection(collectionPath).document(noteId)
        docRef.update("viewCount", FieldValue.increment(1)).await()
    }

    suspend fun updateLikeCount(noteId: String, increment: Int) {
        val docRef = db.collection(collectionPath).document(noteId)
        // increment가 1이면 +1, -1이면 -1
        docRef.update("likeCount", FieldValue.increment(increment.toLong())).await()
    }
}