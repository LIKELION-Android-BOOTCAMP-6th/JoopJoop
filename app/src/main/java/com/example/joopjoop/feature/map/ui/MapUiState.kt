package com.example.joopjoop.feature.map.ui

import com.example.joopjoop.core.common.util.LocationUtil
import com.google.android.gms.maps.model.LatLng

// 1. 임시 DTO를 여기에 정의하여 모든 파일이 이 모델을 바라보게 합니다.
data class NoteDTO(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val category: String = "일상"
)

data class MapUiState(
    val currentUserLocation: LatLng? = null, // F-MAP-02: 현재 사용자 위치 (기준점)
    val mapCenterLocation: LatLng? = null,    // F-MAP-06: 지도 중심 좌표 (재조회용)
    val notes: List<NoteDTO> = emptyList(),   // F-MAP-03: 탐색된 전체 쪽지 리스트 (최대 5km)
    val isLoading: Boolean = false,           // 로딩 상태
    val errorMessage: String? = null,         // 예외 처리 메시지
    val isPermissionGranted: Boolean = false  // F-MAP-01: 권한 허용 여부
) {
    /**
     * [F-MAP-05 관련] 내 위치에서 30m 이내에 있어 '줍기'가 가능한 쪽지들
     */
    val pickableNotes: List<NoteDTO>
        get() = if (currentUserLocation == null) emptyList()
        else notes.filter { note ->
            LocationUtil.calculateDistance(
                currentUserLocation.latitude, currentUserLocation.longitude,
                note.latitude, note.longitude
            ) <= 30f
        }

    /**
     * [F-MAP-03 관련] 지도는 표시되지만, 거리가 멀어(30m 초과) 아직 주울 수 없는 쪽지들
     */
    val distantNotes: List<NoteDTO>
        get() = if (currentUserLocation == null) notes
        else notes.filter { note ->
            LocationUtil.calculateDistance(
                currentUserLocation.latitude, currentUserLocation.longitude,
                note.latitude, note.longitude
            ) > 30f
        }

    /**
     * F-MAP-08: 주변 쪽지 개수 텍스트 계산
     */
    val noteCountText: String
        get() = if (notes.isEmpty()) "주변에 쪽지가 없어요" else "주변에 ${notes.size}개의 쪽지가 있어요"

    /**
     * F-MAP-08: 쪽지 목록 버튼 노출 여부
     */
    val isNoteListButtonVisible: Boolean
        get() = notes.isNotEmpty()
}