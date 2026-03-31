package com.example.joopjoop.feature.map.ui

import com.example.joopjoop.core.model.DialogState
import com.example.joopjoop.core.model.Note
import com.google.android.gms.maps.model.LatLng


data class MapUiState(
    val currentUserLocation: LatLng? = null, // 현재 사용자 위치 (기준점)
    val mapCenterLocation: LatLng? = null,    // 지도 중심 좌표 (재조회용)
    val pickableNotes: List<Note> = emptyList(), // 100m 이내 쪽지
    val distantNotes: List<Note> = emptyList(), // 100m 초과 쪽지
    val isLoading: Boolean = false,           // 로딩 상태
    val errorMessage: String? = null,         // 예외 처리 메시지
    val isPermissionGranted: Boolean = false,  // 권한 허용 여부
    val dialogState: DialogState? = null // 다이얼로그 상태를 관리. null이면 화면에 아무것도 뜨지 않음.
) {
    val noteCountText: String
        get() = if (pickableNotes.isEmpty() && distantNotes.isEmpty())
            "주변에 쪽지가 없어요"
        else
            "주변에 ${pickableNotes.size + distantNotes.size}개의 쪽지가 있어요"

    // 쪽지 목록 버튼 노출 여부
    val isNoteListButtonVisible: Boolean
        get() = (pickableNotes.isNotEmpty() || distantNotes.isNotEmpty())
}