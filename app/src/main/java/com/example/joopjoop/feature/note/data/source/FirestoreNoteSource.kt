package com.example.joopjoop.feature.note.data.source

import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import com.example.joopjoop.core.model.Scrap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date


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
                id = doc.id,
                userNickname = doc.getString("authorName") ?: "",
                createdAt = timestamp?.toDate() ?: Date(),
                likeCount = doc.getLong("likeCount")?.toInt() ?: 0,
                viewCount = doc.getLong("viewCount")?.toInt() ?: 0,
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
            .orderBy("location.geohash")
            .startAt(centerGeohash)
            .endAt(centerGeohash + "\uf8ff")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val createdAt = doc.getTimestamp("createdAt")
            val expiresAt = doc.getTimestamp("expiresAt")

            // 'location'이라는 내부 Map 꺼내기
            val data = doc.data ?: throw Exception("데이터가 없습니다.")
            val locationMap = data["location"] as? Map<String, Any>

            Note(
                id = doc.id,
                userNickname = doc.getString("userNickname") ?: "익명",
                contentText = doc.getString("contentText") ?: "",
                category = doc.getString("category") ?: "일상",
                thumbnailUrl = doc.getString("thumbnailUrl") ?: "",
                location = NoteLocation(
                    address = locationMap?.get("address") as? String ?: "위치 정보 없음",
                    latitude = (locationMap?.get("latitude") as? Number)?.toDouble() ?: 0.0,
                    longitude = (locationMap?.get("longitude") as? Number)?.toDouble() ?: 0.0,
                    geohash = locationMap?.get("geohash") as? String ?: ""
                ),
                createdAt = createdAt?.toDate() ?: Date(),
                expiresAt = expiresAt?.toDate() ?: Date()
            )
        }
    }

    // 쪽지 상세 데이터 조회
    suspend fun getNoteDetail(noteId: String): Note {
        val doc = db.collection(collectionPath).document(noteId).get().await()

        // 문서 데이터 전체를 Map으로 가져옵니다.
        val data = doc.data ?: throw Exception("데이터가 없습니다.")

        // 'location'이라는 내부 Map 꺼내기
        val locationMap = data["location"] as? Map<String, Any>
        val timestamp = doc.getTimestamp("createdAt")
        return Note(
            id = doc.id,
            authorId = doc.getString("authorId") ?: "",
            userNickname = doc.getString("userNickname") ?: "익명 사용자",
            createdAt = timestamp?.toDate() ?: Date(),
            viewCount = doc.getLong("viewCount")?.toInt() ?: 0,
            likeCount = doc.getLong("likeCount")?.toInt() ?: 0,
            contentText = doc.getString("contentText") ?: "내용 없음",
            imageUrl = doc.getString("imageUrl"),
            thumbnailUrl = doc.getString("thumbnailUrl"),
            location = NoteLocation(
                address = locationMap?.get("address") as? String ?: "위치 정보 없음",
                latitude = (locationMap?.get("latitude") as? Number)?.toDouble() ?: 0.0,
                longitude = (locationMap?.get("longitude") as? Number)?.toDouble() ?: 0.0,
                geohash = locationMap?.get("geohash") as? String ?: "",
                distance = locationMap?.get("distance") as? String ?: ""
            )
        )
    }

    // 쪽지 생성
    suspend fun createNote(request: Note): String {
        // 1. ID 자동 생성
        val documentRef = db.collection(collectionPath).document()
        val generatedId = documentRef.id

        // 2. 서버에 저장할 데이터 구성
        val locationMap = hashMapOf(
            "geohash" to request.location.geohash,
            "latitude" to request.location.latitude,
            "longitude" to request.location.longitude,
            "address" to request.location.address, // 지역 동네 문자열
            "distance" to request.location.distance
        )

        val noteData = hashMapOf(
            "id" to generatedId,
            "authorId" to request.authorId,
            "userNickname" to request.userNickname,
            "userProfileImageUrl" to request.userProfileImageUrl,
            "contentText" to request.contentText,
            "thumbnailUrl" to request.thumbnailUrl,
            "imageUrl" to request.imageUrl,
            "category" to request.category,
            "location" to locationMap,
            "isActive" to true,
            "expiresAt" to request.expiresAt,
            "createdAt" to Timestamp.now()
        )

        // 3. Firestore에 저장 (await - 문서 저장 반환)
        documentRef.set(noteData).await()

        return generatedId // 생성된 ID 반환
    }

    // 조회수 증가
    suspend fun incrementViewCount(noteId: String) {
        val docRef = db.collection(collectionPath).document(noteId)
        docRef.update("viewCount", FieldValue.increment(1)).await()
    }

    // 좋아요 증가
    suspend fun updateLikeCount(noteId: String, increment: Int) {
        val docRef = db.collection(collectionPath).document(noteId)
        // increment가 1이면 +1, -1이면 -1
        docRef.update("likeCount", FieldValue.increment(increment.toLong())).await()
    }

    // 쪽지 좋아요 버튼
    suspend fun addLike(noteId: String, userId: String) {
        val userLikeRef = db.collection("users")
            .document(userId)
            .collection("likes")
            .document(noteId)

        val noteRef = db.collection("notes").document(noteId)
        userLikeRef.set(hashMapOf("noteId" to noteId, "timestamp" to FieldValue.serverTimestamp()))
            .await()

        val noteDoc = noteRef.get().await()
        if (noteDoc.exists()) {
            noteRef.update("likeCount", FieldValue.increment(1)).await()
        }
    }

    // 쪽지 좋아요 해제
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

    // 쪽지 좋아요 여부 조회
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

    // 스크랩 하기
    suspend fun saveScrapNote(scrap: Scrap, userId: String) {
        db.collection("users")
            .document(userId)
            .collection("scraps")
            .document(scrap.noteId) // 중복 스크랩 방지
            .set(scrap)
            .await()
    }

    // 스크랩 취소
    suspend fun cancelScrapNote(noteId: String, userId: String) {
        db.collection("users")
            .document(userId)
            .collection("scraps")
            .document(noteId)
            .delete()
            .await()
    }

    // 스크랩 조회
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

    // 쪽지 수정
    suspend fun editNote(noteId: String, request: Note) {
        // todo :: 쪽지 수정 로직 추가
    }


    // 쪽지 삭제
    suspend fun deleteNote(noteId: String) {
        db.collection(collectionPath).document(noteId).delete().await()
    }
}