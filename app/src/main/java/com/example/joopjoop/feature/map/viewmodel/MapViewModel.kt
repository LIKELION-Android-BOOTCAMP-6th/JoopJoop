package com.example.joopjoop.feature.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.common.policy.DistancePolicy
import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.model.DialogState
import com.example.joopjoop.core.model.Note
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
) : ViewModel() {

    // 관찰 가능한 상태 (Screen에서 이 state를 구독)
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    //앱 시작 시 또는 특정 시점에 내 현재 위치를 즉시 가져와 지도를 이동
    //LocationProvider의 suspend 함수를 사용하여 콜백 없이 구현
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

    // F-MAP-06: 지도 중심 좌표를 기준으로 주변 쪽지를 가져옵니다.
    fun loadNotes(center: LatLng) {
        viewModelScope.launch {
            // [1] 로딩 시작 + 현재 지도 중심 좌표 저장
            _uiState.update { it.copy(isLoading = true, mapCenterLocation = center) }

            try {
                // [2] Firestore에서 '근처 후보 쪽지' 조회 (Geohash 기반, 대략적인 범위)
                val notes = noteRepository.getNotesByLocation(
                    center.latitude,
                    center.longitude
                )
                // [3] 현재 사용자 위치 가져오기 (거리 계산 기준점)
                val userLocation = _uiState.value.currentUserLocation

                // [예외 처리] 사용자 위치가 없으면 거리 계산 불가 → 결과 비움
                if (userLocation == null) {
                    _uiState.update {
                        it.copy(
                            pickableNotes = emptyList(),
                            distantNotes = emptyList(),
                            isLoading = false
                        )
                    }
                    return@launch
                }

                // [4] 결과를 담을 리스트 (UI에서 바로 사용할 데이터)
                val pickable = mutableListOf<Note>()
                val distant = mutableListOf<Note>()

                notes.forEach { note ->

                    // 사용자 위치 ↔ 쪽지 위치 간 거리 계산 (단위: meter)
                    val distance = LocationUtil.calculateDistance(
                        userLocation.latitude,
                        userLocation.longitude,
                        note.location.latitude,
                        note.location.longitude
                    )

                    // [정책 1] 5km 탐색 범위 필터
                    // → Geohash는 정확한 거리 보장이 안되므로 한 번 더 필터링
                    if (!DistancePolicy.isWithinSearchRange(distance)) return@forEach

                    // [정책 2] 100m 기준으로 열람 가능 여부 분리
                    if (DistancePolicy.isWithinPickableRange(distance)) {
                        pickable.add(note)
                    } else {
                        // 100m 초과 → 지도에는 보이지만 열람 불가
                        distant.add(note)
                    }
                }
                // 최종 결과를 UI 상태에 반영
                _uiState.update {
                    it.copy(
                        pickableNotes = pickable,
                        distantNotes = distant,
                        isLoading = false,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {
                // [예외 처리] 네트워크/DB 오류 발생 시 에러 메시지 표시
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "loadNotes 오류"
                    )
                }
            }
        }
    }

    // F-MAP-01/02: 권한 및 사용자 위치 업데이트
    fun onLocationUpdated(location: LatLng) {
        _uiState.update { it.copy(currentUserLocation = location) }
    }

    fun askPermissionWithGuide(onConfirmRequest: () -> Unit) {
        _uiState.update {
            it.copy(
                dialogState = DialogState(
                    title = "위치 권한이 필요해요",
                    description = "주변의 쪽지를 탐색하기 위해 위치 권한이 필요합니다.\n동의하시겠습니까?",
                    confirmText = "동의하기",
                    dismissText = "나중에",
                    onConfirm = {
                        onConfirmRequest() // 여기서 실제 시스템 팝업을 띄움
                        dismissDialog()
                    },
                    onDismiss = { dismissDialog() }
                )
            )
        }
    }

    // 다이얼로그를 화면에서 제거합니다.
    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = null) }
    }

    fun onPermissionResult(isGranted: Boolean, onDenied: () -> Unit = {}) {
        _uiState.update { it.copy(isPermissionGranted = isGranted) }

        if (isGranted) {
            fetchCurrentLocation()
        } else {
            // [수정] 시스템 팝업에서 거절당했을 때, 설정을 유도하는 다이얼로그를 띄웁니다.
            showSettingsDialog(onDenied)
        }
    }

    // 권한 거부 후, 사용자를 앱 설정 화면으로 안내하는 다이얼로그
    private fun showSettingsDialog(onOpenSettings: () -> Unit) {
        _uiState.update {
            it.copy(
                dialogState = DialogState(
                    title = "권한 설정 안내",
                    description = "위치 권한이 거부되었습니다.\n원활한 서비스 이용을 위해 설정에서 권한을 허용해주세요.",
                    confirmText = "설정으로 이동",
                    dismissText = "나중에",
                    onConfirm = {
                        onOpenSettings()
                        dismissDialog()
                    },
                    onDismiss = { dismissDialog() }
                )
            )
        }
    }
}