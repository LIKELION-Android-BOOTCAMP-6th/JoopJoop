package com.example.joopjoop.feature.note.data.source

import android.util.Log
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import com.example.joopjoop.core.model.Scrap
import com.example.joopjoop.feature.note.data.model.NoteRequest
import com.google.firebase.Timestamp
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

    // 현재 이 함수는 모든 쪽지를 쿼리
    suspend fun getNotes(): List<Note> {
        val snapshot = db.collection(collectionPath).get().await()
        return snapshot.documents.mapNotNull { doc ->
            val lat = doc.getDouble("latitude") ?: 0.0
            val lng = doc.getDouble("longitude") ?: 0.0
            val timestamp = doc.getTimestamp("createdAt")
            Note(
                noteId = doc.id,
                userNickname = doc.getString("authorName") ?: "",
                createdAt = timestamp?.toDate() ?: Date(),
                likeCount = doc.getLong("likeCount")?.toInt() ?: 0, // 삭제할 것
                viewCount = doc.getLong("viewCount")?.toInt() ?: 0, // 삭제할 것
                contentText = doc.getString("content") ?: "",
                location = NoteLocation(
                    latitude = lat,
                    longitude = lng
                )
            )
        }
    }


    // 위치를 기반으로 쿼리
    suspend fun getNotesByLocation(centerGeohash: String): List<Note> {
        // 5자리 Geohash 접두사로 시작하는 문서들만 쿼리
        val snapshot = db.collection(collectionPath)
            .orderBy("geohash")
            .startAt(centerGeohash)
            .endAt(centerGeohash + "\uf8ff")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val timestamp = doc.getTimestamp("createdAt")
            Note(
                noteId = doc.id,
                userNickname = doc.getString("authorName") ?: "익명",
                contentText = doc.getString("content") ?: "",
                category = doc.getString("category") ?: "일상",
                imageUrl = doc.getString("imageUri"),
                location = NoteLocation(
                    geohash = doc.getString("geohash") ?: "",
                    latitude = doc.getDouble("latitude") ?: 0.0,
                    longitude = doc.getDouble("longitude") ?: 0.0,
                    address = doc.getString("location") ?: "" // DB의 'location' 필드가 주소 문자열임
                ),
                createdAt = timestamp?.toDate() ?: Date()
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
            "createdAt" to Timestamp.now(),
            "latitude" to request.latitude,
            "longitude" to request.longitude,
            "likeCount" to 0,
            "viewCount" to 0
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

    suspend fun addLike(noteId: String, userId: String) {
        val userLikeRef = db.collection("users")
            .document(userId)
            .collection("likes")
            .document(noteId)

        val noteRef = db.collection("notes").document(noteId)
        userLikeRef.set(hashMapOf("noteId" to noteId, "timestamp" to FieldValue.serverTimestamp())).await()

        val noteDoc = noteRef.get().await()
        if (noteDoc.exists()) {
            noteRef.update("likeCount", FieldValue.increment(1)).await()
        }
    }

    suspend fun removeLike(noteId: String, userId: String) {
        val userLikeRef =
            db.collection("users").document(userId).collection("likes").document(noteId)
        val noteRef = db.collection("notes").document(noteId)

        db.runTransaction { transaction ->
            val likeDoc = transaction.get(userLikeRef)
            if (!likeDoc.exists()) return@runTransaction // 이미 없으면 중단

            val noteDoc = transaction.get(noteRef)

            // 1. 좋아요 기록 삭제
            transaction.delete(userLikeRef)

            // 2. 노트 문서가 있을 때만 카운트 감소
            if (noteDoc.exists()) {
                val currentCount = noteDoc.getLong("likeCount") ?: 0
                if (currentCount > 0) {
                    transaction.update(noteRef, "likeCount", currentCount - 1)
                }
            }
        }.await()
    }

    suspend fun checkLikeExists(noteId: String, userId: String): Boolean {
        return try {
            val document = db.collection("users")
                .document(userId)
                .collection("likes")
                .document(noteId)
                .get()
                .await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveScrapNote(scrap: Scrap, userId: String) {
        db.collection("users")
            .document(userId)
            .collection("scraps")
            .document(scrap.noteId) // 중복 스크랩 방지
            .set(scrap)
            .await()
    }

    suspend fun removeBookmark(noteId: String, userId: String) {
        db.collection("users")
            .document(userId)
            .collection("scraps")
            .document(noteId)
            .delete()
            .await()
    }

    suspend fun checkBookmarkExists(noteId: String, userId: String): Boolean {
        return try {
            val document = db.collection("users")
                .document(userId)
                .collection("scraps")
                .document(noteId)
                .get()
                .await()
            document.exists() // 문서있으면 true, 없으면 false
        } catch (e: Exception) {
            false
        }
    }
}