package com.example.joopjoop.core.common.policy

// 쪽지 탐색 범위, 열람가능한 거리 정책
object DistancePolicy {
    const val SEARCH_RADIUS_METERS = 5000f
    const val PICKABLE_RADIUS_METERS = 100f

    fun isWithinPickableRange(distance: Float): Boolean {
        return distance <= PICKABLE_RADIUS_METERS
    }

    fun isWithinSearchRange(distance: Float): Boolean {
        return distance <= SEARCH_RADIUS_METERS
    }
}