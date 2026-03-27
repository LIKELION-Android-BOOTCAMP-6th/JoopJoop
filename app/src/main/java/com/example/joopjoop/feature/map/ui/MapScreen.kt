package com.example.joopjoop.feature.map.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.common.util.PermissionManager
import com.example.joopjoop.feature.map.ui.components.CurrentLocationButton
import com.example.joopjoop.feature.map.ui.components.NearbyNoteCard
import com.example.joopjoop.feature.map.ui.components.NoteMarker
import com.example.joopjoop.feature.map.ui.components.SearchNoteButton
import com.example.joopjoop.feature.map.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * 메인 지도 화면 컴포넌트
 * 역할: 전체 레이아웃 구성, 권한 처리, 컴포넌트 조립 및 뷰모델 상태 연결
 */
@Composable
fun MapScreen(
    viewModel: MapViewModel
) {
    val context = LocalContext.current
    // 뷰모델의 UI 상태 구독
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 지도 카메라 상태 관리 (초기값은 서울시청이나 LaunchedEffect에서 사용자 위치로 업데이트됨)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 16f)
    }

    // --- [권한 및 초기 위치 설정 로직] ---
    LaunchedEffect(Unit) {
        // 1. 위치 권한 확인
        if (PermissionManager.hasLocationPermission(context)) {
            viewModel.onPermissionResult(true)

            // 2. [기능 수정] 사용자의 실제 현재 위치를 가져와 초기화
            LocationUtil.getLastLocation(context) { location ->
                val userLatLng = LatLng(location.latitude, location.longitude)

                // 카메라를 사용자 현재 위치로 즉시 이동
                cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 16f)

                // 뷰모델에 내 위치를 전달하여 30m 이내 '줍기 가능' 쪽지 계산 시작
                viewModel.onLocationUpdated(userLatLng)

                // 초기 진입 시 내 위치 주변의 쪽지 데이터 로드
                viewModel.loadNotes(userLatLng)
            }
        }
    }

    // --- [UI 레이아웃 조립] ---
    Box(modifier = Modifier.fillMaxSize()) {

        // [레이어 1] 구글 지도 영역
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // 권한 허용 시 지도에 파란색 내 위치 점 표시
            properties = MapProperties(isMyLocationEnabled = uiState.isPermissionGranted),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false, // 커스텀 버튼 사용을 위해 기본 버튼 비활성화
                zoomControlsEnabled = false      // UI 단순화를 위해 줌 컨트롤 숨김
            )
        ) {
            // [기능] 상태에 따라 마커를 구분하여 그림 (4번 피드백 반영)

            // 줍기 가능 쪽지 (주황색 마커)
            uiState.pickableNotes.forEach { note ->
                // key(note.id)를 사용하여 마커 렌더링 최적화 유지
                key(note.id) {
                    NoteMarker(note = note, isPickable = true)
                }
            }

            // 거리가 먼 쪽지 (회색 마커)
            uiState.distantNotes.forEach { note ->
                key(note.id) {
                    NoteMarker(note = note, isPickable = false)
                }
            }
        }

        // [레이어 2] 상단 영역: 쪽지 탐색 버튼
        // 현재 카메라가 바라보고 있는 중심점(target)을 기준으로 다시 검색함
        SearchNoteButton(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
            onClick = { viewModel.loadNotes(cameraPositionState.position.target) }
        )

        // [레이어 3] 하단 영역: 내 위치 버튼 및 안내 카드
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.End
        ) {
            // 커스텀 내 위치 FAB 버튼
            CurrentLocationButton(
                onClick = {
                    // 클릭 시 현재 사용자 위치(uiState.currentUserLocation)로 카메라 이동
                    uiState.currentUserLocation?.let { userPos ->
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(userPos, 16f)
                    }
                }
            )

            // 주변에 쪽지가 있는 경우에만 정보 카드 노출
            if (uiState.isNoteListButtonVisible) {
                Spacer(modifier = Modifier.height(16.dp))
                NearbyNoteCard(
                    noteCountText = uiState.noteCountText, // "주변에 n개의 쪽지가 있어요"
                    onViewListClick = {
                        /* TODO: 쪽지 목록 화면 이동 내비게이션 로직 */
                    }
                )
            }
        }
    }
}