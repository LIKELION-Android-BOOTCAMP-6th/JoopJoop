package com.example.joopjoop.feature.map.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.joopjoop.core.common.util.PermissionManager
import com.example.joopjoop.feature.map.viewmodel.MapViewModel // ViewModel 임포트
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// 시안 테마 컬러
val JoopJoopOrange = Color(0xFFE67E22)
val JoopJoopDark = Color(0xFF1C1C1E)

@Composable
fun MapMockScreen(
    // ViewModel 주입 (Hilt 도입 전이라면 기본 viewModel() 사용)
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    // ViewModel의 상태를 구독
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. 지도 카메라 상태: 초기 위치를 서울시청으로 설정하고 줌 레벨을 16f로
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 16f)
    }

    // --- [권한 요청 프로세스] ---
    // 런처 정의: 사용자가 권한 팝업에서 선택한 결과를 받아 상태를 업데이트함
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.all { it }
        viewModel.onPermissionResult(isGranted) // 결과값을 ViewModel로 전달
        if (isGranted) {
            viewModel.loadNotes(cameraPositionState.position.target)
        }
    }

    // LaunchedEffect의 키를 Unit으로 설정하여 화면 진입 시 무조건 체크하게 합니다.
    LaunchedEffect(Unit) {
        if (PermissionManager.hasLocationPermission(context)) {
            viewModel.onPermissionResult(true)
            // 권한이 있다면 즉시 현재 카메라 중심 위치로 탐색 시작
            viewModel.onLocationUpdated(cameraPositionState.position.target)
            viewModel.loadNotes(cameraPositionState.position.target)
        }
    }


    // --- [전체 레이아웃 구성] ---
    Box(modifier = Modifier.fillMaxSize()) {

        // [레이어 1] 구글 지도 영역
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // 권한 허용 시 지도에 내 위치 파란 점 표시
            properties = MapProperties(isMyLocationEnabled = uiState.isPermissionGranted),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false, // 시안의 커스텀 버튼을 쓰기 위해 기본 버튼 비활성화
                zoomControlsEnabled = false // UI 깔끔함을 위해 줌 버튼 숨김
            )
        ) {
            // UI 상태에서 분리된 '줍기 가능' 쪽지와 '먼' 쪽지를 각각 그려줌
            // 줍기 가능 (주황색 마커)
            uiState.pickableNotes.forEach { note ->
                // key()를 사용해 Compose 재랜더링 시 마커의 고유성 보장 (에러 방지용)
                key(note.id) {
                    MarkerComposable(
                        state = MarkerState(position = LatLng(note.latitude, note.longitude))
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(JoopJoopOrange, CircleShape)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 현재 ic_note 리소스가 없으므로 이메일 아이콘으로 대체함
                            Icon(Icons.Default.Email, null, tint = Color.White)
                        }
                    }
                }
            }
            // 멀리 있는 쪽지 (회색 마커)
            uiState.distantNotes.forEach { note ->
                key(note.id) {
                    MarkerComposable(
                        state = MarkerState(
                            position = LatLng(
                                note.latitude,
                                note.longitude
                            )
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Gray, CircleShape)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Email, null, tint = Color.White)
                        }
                    }
                }
            }
        }

        // [레이어 2] 상단: 쪽지 탐색 버튼 (F-MAP-06)
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
                .clip(RoundedCornerShape(24.dp))
                .clickable {
                    viewModel.loadNotes(cameraPositionState.position.target)
                }, // ViewModel에 요청
            color = Color.Black.copy(alpha = 0.7f), // 반투명 블랙 배경
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("쪽지 탐색", style = MaterialTheme.typography.labelLarge)
            }
        }

        // [레이어 3] 하단 그룹: 내 위치 버튼 + 안내 카드
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.End // 버튼을 오른쪽 정렬
        ) {
            // 시안에 있던 흰색 원형 내 위치 버튼
            FloatingActionButton(
                onClick = { /* 카메라 내 위치 이동 로직 */ },
                containerColor = Color.White,
                contentColor = JoopJoopDark,
                shape = CircleShape,
                modifier = Modifier.size(54.dp)
            ) {
                // MyLocation 아이콘 라이브러리 부재로 LocationOn으로 대체
                Icon(Icons.Default.LocationOn, "내 위치")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 주변 쪽지 안내 카드 (F-MAP-08)
            if (uiState.isNoteListButtonVisible) { // ViewModel에서 계산된 가시성 여부 사용
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = JoopJoopDark),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 왼쪽 주황색 배경의 아이콘 영역
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(JoopJoopOrange.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Email, null, tint = JoopJoopOrange)
                        }
                        Spacer(modifier = Modifier.width(16.dp))

                        // 텍스트 및 이동 버튼 영역
                        Column(modifier = Modifier.weight(1f)) {
                            // UI 상태에서 제공하는 텍스트 사용
                            Text(
                                uiState.noteCountText,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Someone left a message here",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // 목록 화면으로 가는 주황색 버튼
                            Button(
                                onClick = { /* 리스트 이동 */ },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = JoopJoopOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("View nearby notes")
                            }
                        }
                    }
                }
            }
        }
    }
}