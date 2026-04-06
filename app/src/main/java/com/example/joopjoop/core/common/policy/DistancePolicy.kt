package com.example.joopjoop.core.common.policy

// 쪽지 탐색 범위, 열람가능한 거리 정책
object DistancePolicy {
    const val SEARCH_RADIUS_METERS = 2500f // 반지름
    const val PICKABLE_RADIUS_METERS = 100f

    // 탐색 범위 체크
    fun isWithinSearchRange(distance: Float): Boolean = distance <= SEARCH_RADIUS_METERS

    // 내 쪽지거나 이미 계산된 거리(Float)가 있을 때 열람 가능 여부 체크
    fun isPickable(isMyNote: Boolean, distance: Float): Boolean =
        isMyNote || distance <= PICKABLE_RADIUS_METERS
}