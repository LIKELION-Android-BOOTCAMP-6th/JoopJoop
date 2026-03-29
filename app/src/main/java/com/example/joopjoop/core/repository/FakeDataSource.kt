package com.example.joopjoop.core.repository

import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import java.util.Date
import kotlin.random.Random

object FakeDataSource {
    fun getFakeNotes(baseLat: Double, baseLng: Double): List<Note> {
        val notes = mutableListOf<Note>()

        // 줍기 가능한 아주 가까운 쪽지 (30m 이내) - 최소 2개 보장
        repeat(2) { i ->
            val (lat, lng) = getRandomLocation(baseLat, baseLng, 20.0) // 20m 이내
            notes.add(createMockNote("near_pickup_${i + 1}", lat, lng))
        }

        // 5km 이내의 검색되는 쪽지 샘플 15개 생성
        repeat(15) { i ->
            val (lat, lng) = getRandomLocation(baseLat, baseLng, 2000.0)
            notes.add(createMockNote("fake_${i + 1}", lat, lng))
        }
        return notes
    }

    private fun createMockNote(id: String, lat: Double, lng: Double): Note {
        // 70% 확률로 이미지가 있고, 30% 확률로 텍스트만 있는 경우 시뮬레이션
        val hasImage = Random.nextInt(100) < 70
        val randomImageUrl = if (hasImage) {
            // picsum.photos를 사용하여 id 기반으로 고유한 랜덤 이미지 할당
            "https://picsum.photos/seed/$id/400/300"
        } else {
            null
        }

        return Note(
            noteId = id,
            userId = "user_mock",
            userNickname = "줍줍이",
            userProfileImageUrl = "",
            contentText = "이것은 $id 번 쪽지입니다.",
            imageUrl = null,
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
            createdAt = Date(),
            expiresAt = Date(System.currentTimeMillis() + 86400000)
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