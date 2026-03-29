package com.example.joopjoop.feature.map.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.joopjoop.core.common.util.PermissionManager
import com.example.joopjoop.core.designsystem.components.JoopJoopDialog
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
    viewModel: MapViewModel,
    onNavigateToNoteList: () -> Unit, // ← 리스트 화면 이동을 위한 콜백
    onNavigateToNoteDetail: (String) -> Unit // 상세 이동 콜백
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. 권한 요청 런처 (시스템 팝업용)
    val permissionLauncher = PermissionManager.rememberLocationPermissionLauncher { isGranted ->
        // 결과가 나오면 viewModel에 전달 (시스템 설정 이동 로직 포함)
        viewModel.onPermissionResult(isGranted) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
    }

    // [추가] 카메라가 이미 사용자를 찾았는지 기억하는 상태 (화면 재생성 시에도 유지하려면 rememberSaveable 사용)
    var hasMovedToUserLocation by rememberSaveable { mutableStateOf(false) }

    // 지도 카메라 상태 관리 (초기값은 서울시청이나 LaunchedEffect에서 사용자 위치로 업데이트됨)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 16f)
    }

    // [카메라 제어] 위치 업데이트 시 최초 1회만 이동
    LaunchedEffect(uiState.currentUserLocation) {
        val userPos = uiState.currentUserLocation
        if (userPos != null && !hasMovedToUserLocation) {
            // 앱 실행 후 '딱 한 번'만 내 위치로 카메라를 옮깁니다.
            // animate 대신 직접 position을 대입하면 화면 전환 시 날아가는 현상을 방지할 수 있음
            cameraPositionState.position = CameraPosition.fromLatLngZoom(userPos, 16f)
            hasMovedToUserLocation = true
        }
    }

    // --- [권한 및 초기 위치 설정 로직] ---
    LaunchedEffect(Unit) {
        // 권한 확인
        if (PermissionManager.hasLocationPermission(context)) {
            viewModel.onPermissionResult(true)
            // 이미 위치 정보가 있다면(뒤로가기로 돌아온 경우) 다시 요청하지 않음
            if (uiState.currentUserLocation == null) {
                viewModel.fetchCurrentLocation()
            }
        } else {
            // [변경 포인트] 팝업을 바로 띄우지 않고, '사전 안내 다이얼로그'
            viewModel.askPermissionWithGuide {
                // 사용자가 다이얼로그에서 '동의하기'를 눌렀을 때만 실제 시스템 팝업을 호출
                permissionLauncher.launch(PermissionManager.locationPermissions)
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
                key(note.noteId) {
                    NoteMarker(
                        note = note,
                        isPickable = true,
                        onClick = { onNavigateToNoteDetail(note.noteId) } // 클릭 시 ID 전달
                    )
                }
            }

            // 거리가 먼 쪽지 (회색 마커)
            uiState.distantNotes.forEach { note ->
                key(note.noteId) {
                    NoteMarker(
                        note = note,
                        isPickable = false,
                        onClick = { /* 거리가 멀면 클릭 안되게 하거나 안내 메시지 */ }
                    )
                }
            }
        }

        // [레이어 2] 상단 영역: 쪽지 탐색 버튼
        // 현재 카메라가 바라보고 있는 중심점(target)을 기준으로 다시 검색함
        SearchNoteButton(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
            onClick = {
                // [권한 체크 추가]
                if (PermissionManager.hasLocationPermission(context)) {
                    viewModel.loadNotes(cameraPositionState.position.target)
                } else {
                    // 권한이 없으면 안내 다이얼로그 노출 -> 승인 시 시스템 팝업
                    viewModel.askPermissionWithGuide {
                        permissionLauncher.launch(PermissionManager.locationPermissions)
                    }
                }
            }
        )

        // [레이어 3] 하단 영역: 내 위치 버튼 및 안내 카드
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd) // 컬럼 자체를 '우측 하단'에 고정 (버튼 위치 사수)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.End
        ) {
            // 커스텀 내 위치 FAB 버튼
            CurrentLocationButton(
                onClick = {
                    if (PermissionManager.hasLocationPermission(context)) {
                        uiState.currentUserLocation?.let { userPos ->
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(userPos, 16f)
                        } ?: viewModel.fetchCurrentLocation()
                    } else {
                        viewModel.askPermissionWithGuide {
                            permissionLauncher.launch(PermissionManager.locationPermissions)
                        }
                    }
                }
            )

            // 주변에 쪽지가 있는 경우에만 정보 카드 노출
            if (uiState.isNoteListButtonVisible) {
                Spacer(modifier = Modifier.height(16.dp))
                NearbyNoteCard(
                    noteCountText = uiState.noteCountText,
                    onViewListClick = {
                        onNavigateToNoteList()
                    }
                )
            }
        }

        // uiState.dialogState가 null이 아닐 때(값이 있을 때)만 다이얼로그를 띄움
        uiState.dialogState?.let { config ->
            JoopJoopDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = config.title,
                description = config.description,
                confirmText = config.confirmText,
                dismissText = config.dismissText,
                onConfirm = config.onConfirm,
                onDismiss = config.onDismiss
            )
        }
    }
}