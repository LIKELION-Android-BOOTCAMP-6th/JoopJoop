package com.example.joopjoop.feature.note.data.source

import android.util.Log
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import com.example.joopjoop.core.model.Scrap
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date


class FirestoreNoteSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val collectionPath = "notes"

//    // 위치를 기반으로 쿼리
//    suspend fun getNotesByLocation(centerGeohash: String): List<Note> {
//        // 5자리 Geohash 접두사로 시작하는 문서들만 쿼리
//        val snapshot = db.collection(collectionPath)
//            .orderBy("location.geohash")
//            .startAt(centerGeohash)
//            .endAt(centerGeohash + "\uf8ff")
//            .get()
//            .await()
//
//        return snapshot.documents.mapNotNull { doc ->
//            val createdAt = doc.getTimestamp("createdAt")
//            val expiresAt = doc.getTimestamp("expiresAt")
//
//            // 2. 이제 Timestamp끼리 비교가 가능합니다.
//            if (expiresAt != null && expiresAt.compareTo(now()) < 0) {
//                return@mapNotNull null
//            }
//
//            // 'location'이라는 내부 Map 꺼내기
//            val data = doc.data ?: throw Exception("데이터가 없습니다.")
//            val locationMap = data["location"] as? Map<String, Any>
//
//            Note(
//                id = doc.id,
//                authorId = doc.getString("authorId") ?: "",
//                userNickname = doc.getString("userNickname") ?: "익명",
//                profileImageUrl = doc.getString("profileImageUrl") ?: "",
//                contentText = doc.getString("contentText") ?: "",
//                category = doc.getString("category") ?: "일상",
//                thumbnailUrl = doc.getString("thumbnailUrl") ?: "",
//                location = NoteLocation(
//                    address = locationMap?.get("address") as? String ?: "위치 정보 없음",
//                    latitude = (locationMap?.get("latitude") as? Number)?.toDouble() ?: 0.0,
//                    longitude = (locationMap?.get("longitude") as? Number)?.toDouble() ?: 0.0,
//                    geohash = locationMap?.get("geohash") as? String ?: ""
//                ),
//                createdAt = createdAt?.toDate() ?: Date(),
//                expiresAt = expiresAt?.toDate() ?: Date(),
//                viewCount = doc.getLong("viewCount")?.toInt() ?: 0,
//                likeCount = doc.getLong("likeCount")?.toInt() ?: 0
//            )
//        }
//    }
//    // 쪽지 탐색 쿼리 함수끼리 붙여놓기 위해 코드 내 위치만 이동시킴
//    suspend fun getVisibleNotes(
//        lat: Double,
//        lng: Double,
//        myUid: String
//    ): List<Note> {
//        // 1. 좌표를 Geohash 문자열로 변환
//        val precision5Geohash = getGeohash(lat, lng).take(5)
//
//        // 2. 내 위치 주변(5km 반경) 쪽지 가져오기
//        val nearbyNotes = getNotesByLocation(precision5Geohash)
//
//        // 3. 내 쪽지도 '현재 지도 범위(Geohash)' 내에 있는 것만 가져오기
//        val myNotes = if (myUid.isNotEmpty()) {
//            try {
//                db.collection(collectionPath)
//                    .whereEqualTo("authorId", myUid)
//                    .whereGreaterThanOrEqualTo("location.geohash", precision5Geohash) // 경로 수정
//                    .whereLessThanOrEqualTo(
//                        "location.geohash",
//                        precision5Geohash + "\uf8ff"
//                    )
//                    .get()
//                    .await()
//                    .documents.mapNotNull { mapDocumentToNote(it) }
//            } catch (e: Exception) {
//                Log.e("Firestore", "myNotes 쿼리 실패(인덱스 확인 필요): ${e.message}")
//                emptyList()
//            }
//        } else emptyList()
//
//        // 4. 두 리스트를 합치기 + 중복 제거 + 만료 시간 체크
//        val currentTime = System.currentTimeMillis()
//
//        return (nearbyNotes + myNotes)
//            .distinctBy { it.id } // 중복된 쪽지 제거
//            .filter { it.expiresAt.time > currentTime } // 만료되지 않은 것만 필터링
//            .sortedByDescending { it.createdAt } // 최신순 정렬
//    }

    // 함수명을 변경했습니다.
    // getVisibleNotes --> getNotesByLocation
    // 사용자 위치 중심으로 주변 쪽지 쿼리 ( 내 쪽지 포함 )
    suspend fun getNotesByLocation(
        lat: Double,
        lng: Double,
        myUid: String
    ): List<Note> {
        // 1. 컴파일 에러 해결: GeoLocation 객체를 명확히 생성
        val centerLoc = GeoLocation(lat, lng)
        val radiusInMeters = 2500.0 // 반지름 2.5km (DistancePolicy 반영)

        // 2. 9개 격자 범위 계산
        val bounds =
            GeoFireUtils.getGeoHashQueryBounds(centerLoc, radiusInMeters)

        // 복합 인덱스 쿼리 조건 및 순서
        val tasks = bounds.map { b ->
            db.collection(collectionPath)
                .whereEqualTo("isActive", true) // 삭제되지 않은 쪽지
                .orderBy("location.geohash")
                .startAt(b.startHash)    // 정렬 순서에 맞춰 시작점 지정
                .endAt(b.endHash)        // 정렬 순서에 맞춰 끝점 지정
                .get()
        }
        val snapshots =
            Tasks.whenAllSuccess<QuerySnapshot>(tasks).await()
        val currentTime = System.currentTimeMillis()

        return snapshots.flatMap { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                val note = mapDocumentToNote(doc) ?: return@mapNotNull null // 맵핑
                if (note.expiresAt.time <= currentTime) return@mapNotNull null // 쿼리에서 못 거른 시간 만료만 체크
                note // 여기서 isPickable 계산 안 함! 뷰모델에 맡김.
            }
        }.distinctBy { it.id } // 중복제거
    }

    // 쪽지 상세 데이터 조회
    suspend fun getNoteDetail(noteId: String): Note {
        val docRef = db.collection(collectionPath).document(noteId)

        // 조회 요청 - 조회수 +1
        docRef.update("viewCount", FieldValue.increment(1)).await()

        val doc = docRef.get().await()
        // 문서 데이터 전체를 Map으로 가져옵니다.
        val data = doc.data ?: throw Exception("데이터가 없습니다.")

        // 'location'이라는 내부 Map 꺼내기
        val locationMap = data["location"] as? Map<String, Any>
        val timestamp = doc.getTimestamp("createdAt")
        return Note(
            id = doc.id,
            authorId = doc.getString("authorId") ?: "",
            userNickname = doc.getString("userNickname") ?: "익명 사용자",
            category = doc.getString("category") ?: "일상",
            profileImageUrl = doc.getString("profileImageUrl") ?: "",
            createdAt = timestamp?.toDate() ?: Date(),
            viewCount = doc.getLong("viewCount")?.toInt() ?: 0,
            likeCount = doc.getLong("likeCount")?.toInt() ?: 0,
            contentText = doc.getString("contentText") ?: "내용 없음",
            imageUrl = doc.getString("imageUrl"),
            thumbnailUrl = doc.getString("thumbnailUrl"),
            storageHours = doc.getLong("storageHours")?.toInt() ?: 0,
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
    suspend fun createNote(request: Note) {
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
            "profileImageUrl" to request.profileImageUrl,
            "contentText" to request.contentText,
            "thumbnailUrl" to request.thumbnailUrl,
            "imageUrl" to request.imageUrl,
            "category" to request.category,
            "location" to locationMap,
            "isActive" to true,
            "storageHours" to request.storageHours,
            "expiresAt" to request.expiresAt,
            "createdAt" to Timestamp.now()
        )

        // 3. Firestore에 저장 (await - 문서 저장 반환)
        documentRef.set(noteData).await()
    }

    suspend fun uploadImage(
        originalData: ByteArray,    // 원본 데이터
        thumbnailData: ByteArray,   // 썸네일 데이터
        fileName: String,
        onProgress: (Float) -> Unit
    ): Pair<String, String>? { // 두 개의 URL을 반환하도록 변경
        return try {
            // 경로를 각각 다르게 설정 (폴더 분리)
            val originalRef = storage.reference.child("notes/images/$fileName.jpg")
            val thumbnailRef = storage.reference.child("notes/thumbnails/${fileName}_thumb.jpg")

            // 1. 원본 업로드 (진행률은 원본 기준으로 표시)
            val originalTask = originalRef.putBytes(originalData)
            originalTask.addOnProgressListener { taskSnapshot ->
                val progress =
                    (taskSnapshot.bytesTransferred.toDouble() / taskSnapshot.totalByteCount.toDouble()).toFloat()
                onProgress(progress)
            }.await()
            val originalUrl = originalRef.downloadUrl.await().toString()

            // 2. 썸네일 업로드
            thumbnailRef.putBytes(thumbnailData).await()
            val thumbnailUrl = thumbnailRef.downloadUrl.await().toString()

            // 두 URL을 묶어서 반환
            Pair(originalUrl, thumbnailUrl)

        } catch (e: Exception) {
            Log.e("PhotoDebug", "업로드 중 에러: ${e.message}")
            null
        }
    }

    suspend fun getNotesByAuthor(authorId: String): List<Note> {
        return try {
            val snapshot = db.collection(collectionPath)
                .whereEqualTo("authorId", authorId)
                .whereGreaterThan("expiresAt", Timestamp.now()) // 만료 전인 것만
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                mapDocumentToNote(doc) // 중복 코드 없이 깔끔하게 호출
            }
        } catch (e: Exception) {
            Log.e("FirestoreNoteSource", "getNotesByAuthor Error: ${e.message}")
            emptyList()
        }
    }

    // FirestoreNoteSource.kt 클래스 내부 하단에 추가
    private fun mapDocumentToNote(doc: com.google.firebase.firestore.DocumentSnapshot): Note? {
        return try {
            val data = doc.data ?: return null

            // 1. location 맵 데이터 추출
            val locationMap = data["location"] as? Map<String, Any>

            // 2. 개별 필드 추출
            val noteLocation = NoteLocation(
                geohash = locationMap?.get("geohash") as? String ?: "",
                latitude = (locationMap?.get("latitude") as? Number)?.toDouble() ?: 0.0,
                longitude = (locationMap?.get("longitude") as? Number)?.toDouble() ?: 0.0,
                address = locationMap?.get("address") as? String ?: "",
                distance = locationMap?.get("distance") as? String ?: ""
            )

            val createdAt = doc.getTimestamp("createdAt")?.toDate() ?: Date()
            val expiresAt = doc.getTimestamp("expiresAt")?.toDate() ?: Date()

            // 3. Note 객체 생성 및 반환
            Note(
                id = doc.id,
                authorId = doc.getString("authorId") ?: "",
                userNickname = doc.getString("userNickname") ?: "익명",
                profileImageUrl = doc.getString("profileImageUrl"), // 최상위에 위치
                contentText = doc.getString("contentText") ?: "",
                thumbnailUrl = doc.getString("thumbnailUrl"),
                imageUrl = doc.getString("imageUrl"),
                category = doc.getString("category") ?: "일상",
                viewCount = doc.getLong("viewCount")?.toInt() ?: 0,
                likeCount = doc.getLong("likeCount")?.toInt() ?: 0,
                location = noteLocation,
                isActive = doc.getBoolean("isActive") ?: true,
                storageHours = (doc.get("storageHours") as? Number)?.toInt() ?: 12,
                createdAt = doc.getTimestamp("createdAt")?.toDate() ?: Date(),
                expiresAt = doc.getTimestamp("expiresAt")?.toDate() ?: Date()
            )
        } catch (e: Exception) {
            Log.e("FirestoreNoteSource", "Mapping Error: ${e.message}")
            null
        }
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

    // 쪽지 삭제
    suspend fun deleteNote(noteId: String): Boolean {
        return try {
            db.collection(collectionPath)
                .document(noteId)
                .update("isActive", false)
                .await()
            true
        } catch (e: Exception) {
            // 실패 시 에러 처리 (로그 기록 등)
            Log.e("Firestore", "쪽지 삭제 실패 : ${e.localizedMessage}", e)
            false
        }
    }

    // 수정한 쪽지 제출
    suspend fun submitEditedNote(noteId: String, updatedNote: Note) {
        db.collection("notes")
            .document(noteId)
            .set(updatedNote)
            .await()
    }

    // 특정 유저의 게시물 개수 조회
    suspend fun getUserNoteCount(uid: String): Int {
        return try {
            val snapshot = db.collection(collectionPath)
                .whereEqualTo("authorId", uid)
                .whereEqualTo("isActive", true)
                .count()
                .get(AggregateSource.SERVER)
                .await()

            snapshot.count.toInt()
        } catch (e: Exception) {
            Log.e("FirestoreNoteSource", "개수 조회 실패: ${e.message}")
            0
        }
    }
}