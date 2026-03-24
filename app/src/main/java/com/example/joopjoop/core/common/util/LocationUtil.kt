package com.example.joopjoop.core.common.util


import android.location.Location
import kotlin.math.*

object LocationUtil {

    /**
     * 두 지점 간의 직선 거리(미터)를 계산합니다. (Haversine 공식 사용)
     * F-MAP-03(5km 필터), F-MAP-05(30m 줍기 검증)에 사용됩니다.
     */
    fun calculateDistance(
        startLat: Double, startLng: Double,
        endLat: Double, endLng: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLat, startLng, endLat, endLng, results)
        return results[0]
    }

    /**
     * Geohash 생성 (Firestore 쿼리용)
     * 위치 좌표를 문자열 형태의 Geohash로 변환합니다.
     * (직접 구현하거나 외부 라이브러리를 활용할 수 있습니다.)
     */
    fun getGeohash(lat: Double, lng: Double): String {
        // Geohash는 라이브러리를 쓰거나 특정 알고리즘이 필요
        // 좌표 기반 쿼리를 위해 틀만 잡아둠.
        return ""
    }

    /**
     * 거리를 사람이 읽기 좋은 텍스트로 변환합니다.
     * 예: 30m, 1.2km 등
     */
    fun formatDistance(distanceInMeters: Float): String {
        return if (distanceInMeters < 1000) {
            "${distanceInMeters.toInt()}m"
        } else {
            "%.1fkm".format(distanceInMeters / 1000)
        }
    }
}