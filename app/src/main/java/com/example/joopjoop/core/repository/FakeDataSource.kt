package com.example.joopjoop.core.repository

import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import java.util.Date
import kotlin.random.Random

object FakeDataSource {
    fun getFakeNotes(baseLat: Double, baseLng: Double): List<Note> {
        val notes = mutableListOf<Note>()

        // - 20m 이내에 최소 2개 생성
        repeat(2) { i ->
            val (lat, lng) = getRandomLocation(baseLat, baseLng, 20.0)

            notes.add(
                createMockNote(
                    id = "near_pickup_${i + 1}",
                    lat = lat,
                    lng = lng,
                )
            )
        }
        // - 최대 약 2km 범위 내 랜덤 생성
        repeat(15) { i ->
            val (lat, lng) = getRandomLocation(baseLat, baseLng, 2000.0)

            notes.add(
                createMockNote(
                    id = "fake_${i + 1}",
                    lat = lat,
                    lng = lng,
                )
            )
        }
        return notes
    }

    private fun createMockNote(id: String, lat: Double, lng: Double): Note {

        val now = Date() // 생성 기준 시간 (createdAt & expiresAt 동일 기준)
        val hasImage = Random.nextInt(100) < 70 // 70% 확률로 이미지 포함 (UI 테스트용)

        val randomImageUrl = if (hasImage) {
            "https://picsum.photos/seed/$id/400/300"
        } else {
            null
        }

        val hours = listOf(3, 6, 9, 12).random()
        val expiresAt = Date(System.currentTimeMillis() + hours * 60 * 60 * 1000)

        return Note(
            id = id,
            authorId = "user_mock",
            userNickname = "줍줍이",
            userProfileImageUrl = "",
            contentText = "이것은 $id 번 쪽지입니다.",
            thumbnailUrl = randomImageUrl,
            imageUrl = randomImageUrl,       // 랜덤 이미지
            category = "일상",
            viewCount = 0,
            likeCount = 0,
            location = NoteLocation(
                geohash = LocationUtil.getGeohash(lat, lng),
                latitude = lat,
                longitude = lng,
                address = "테스트 위치",
                distance = ""
            ),
            isActive = true,
            createdAt = now,
            expiresAt = expiresAt
        )
    }

    private fun getRandomLocation(
        baseLat: Double,
        baseLng: Double,
        radiusInMeters: Double
    ): Pair<Double, Double> {
        val radiusInDegrees = radiusInMeters / 111320.0
        return Pair(
            baseLat + (Random.nextDouble() - 0.5) * radiusInDegrees,
            baseLng + (Random.nextDouble() - 0.5) * radiusInDegrees
        )
    }
}