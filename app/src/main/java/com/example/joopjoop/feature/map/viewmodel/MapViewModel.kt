package com.example.joopjoop.feature.map.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.map.ui.MapUiState
import com.example.joopjoop.feature.map.ui.NoteDTO // MapUiState에 있는 DTO를 가져옴
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MapViewModel(
    private val noteRepository: NoteRepository
): ViewModel() {

    // 관찰 가능한 상태 (Screen에서 이 state를 구독)
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * F-MAP-06: 지도 중심 좌표를 기준으로 주변 쪽지를 가져옵니다.
     */
    fun loadNotes(center: LatLng) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mapCenterLocation = center) }

            // 실제로는 repository.fetchNearbyNotes(center)가 들어갈 자리
            // 목업 데이터
            val mockNotes = List(10) { i ->
                NoteDTO(
                    id = "$i",
                    latitude = center.latitude + (Math.random() - 0.5) * 0.002,
                    longitude = center.longitude + (Math.random() - 0.5) * 0.002
                )
            }

            _uiState.update {
                it.copy(notes = mockNotes, isLoading = false)
            }
        }
    }

    /**
     * F-MAP-01/02: 권한 및 사용자 위치 업데이트
     */
    fun onLocationUpdated(location: LatLng) {
        _uiState.update { it.copy(currentUserLocation = location) }
    }

    fun onPermissionResult(isGranted: Boolean) {
        _uiState.update { it.copy(isPermissionGranted = isGranted) }
    }
}