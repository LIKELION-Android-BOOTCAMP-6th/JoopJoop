package com.example.joopjoop.feature.map.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.data.location.LocationProvider
import com.example.joopjoop.feature.map.ui.MapUiState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MapViewModel(
    private val noteRepository: NoteRepository,
    private val locationProvider: LocationProvider
): ViewModel() {

    // 관찰 가능한 상태 (Screen에서 이 state를 구독)
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * 앱 시작 시 또는 특정 시점에 내 현재 위치를 즉시 가져와 지도를 이동
     * LocationProvider의 suspend 함수를 사용하여 콜백 없이 구현
     */
    fun fetchCurrentLocation() {
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                _uiState.update { state ->
                    state.copy(currentUserLocation = currentLatLng)
                }
                // 위치를 가져오면 해당 지점을 중심으로 주변 쪽지도 함께 로드
                loadNotes(currentLatLng)
            }
        }
    }

    /**
     * F-MAP-06: 지도 중심 좌표를 기준으로 주변 쪽지를 가져옵니다.
     */
    fun loadNotes(center: LatLng) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mapCenterLocation = center) }

            try {
                val notes = noteRepository.getNotesByLocation(center.latitude, center.longitude)

                _uiState.update {
                    it.copy(
                        notes = notes,
                        isLoading = false,
                        errorMessage = null // 성공 시 에러 메시지 초기화
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
//
//            // 실제로는 repository.fetchNearbyNotes(center)가 들어갈 자리
//            // 목업 데이터
//            val mockNotes = List(10) { i ->
//                NoteDTO(
//                    id = "$i",
//                    latitude = center.latitude + (Math.random() - 0.5) * 0.002,
//                    longitude = center.longitude + (Math.random() - 0.5) * 0.002
//                )
//            }
//
//            _uiState.update {
//                it.copy(notes = mockNotes, isLoading = false)
//            }
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
        // 권한이 허용되면 즉시 현재 위치를 가져옴
        if (isGranted) {
            fetchCurrentLocation()
        }
    }
}