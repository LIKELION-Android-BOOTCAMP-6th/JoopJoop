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
import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.common.util.PermissionManager
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// 시안 테마 컬러
val JoopJoopOrange = Color(0xFFE67E22)
val JoopJoopDark = Color(0xFF1C1C1E)

data class MockNote(
    val id: String,
    val lat: Double,
    val lng: Double,
    val category: String
)

@Composable
fun MapMockScreen() {
    val context = LocalContext.current

    // 1. 지도 카메라 상태: 초기 위치를 서울시청으로 설정하고 줌 레벨을 16f로
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 16f)
    }

    // --- [상태 관리 변수들] ---
    // 권한 허용 여부 (F-MAP-01)
    var isPermissionGranted by remember { mutableStateOf(false) }
    // 가짜 사용자 위치 (서울 시청 정문)
    val userLat = 37.5665
    val userLng = 126.9780

    // 지도에 뿌려질 10개의 가짜 쪽지 리스트
    var mockNotes by remember { mutableStateOf<List<MockNote>>(emptyList()) }

    // --- [데이터 로드 함수] ---
    // 인자로 받은 중심 좌표(LatLng)를 기준으로 주변 10개 지점에 쪽지를 생성함
    fun loadMockData(center: LatLng) {
        mockNotes = listOf(
            MockNote("1", center.latitude + 0.0001, center.longitude + 0.0001, "일상"), // 30m 이내
            MockNote("2", center.latitude + 0.0002, center.longitude - 0.0001, "꿀팁"), // 30m 이내
            MockNote("3", center.latitude + 0.0008, center.longitude + 0.0005, "질문"), // 30m 밖
            MockNote("4", center.latitude - 0.0009, center.longitude + 0.0010, "일상"),
            MockNote("5", center.latitude + 0.0015, center.longitude - 0.0012, "꿀팁"),
            MockNote("6", center.latitude - 0.0020, center.longitude + 0.0020, "질문"),
            MockNote("7", center.latitude + 0.0025, center.longitude + 0.0005, "일상"),
            MockNote("8", center.latitude - 0.0010, center.longitude - 0.0025, "꿀팁"),
            MockNote("9", center.latitude + 0.0035, center.longitude - 0.0001, "질문"),
            MockNote("10", center.latitude - 0.0030, center.longitude + 0.0030, "일상")
        )
    }

    // --- [권한 요청 프로세스] ---
    // 런처 정의: 사용자가 권한 팝업에서 선택한 결과를 받아 상태를 업데이트함
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isPermissionGranted = permissions.values.all { it }
        if (isPermissionGranted) loadMockData(LatLng(userLat, userLng))
    }

    // 화면 진입 시 단 한 번 실행: 권한이 이미 있다면 데이터를 바로 불러옴
    LaunchedEffect(Unit) {
        if (PermissionManager.hasLocationPermission(context)) {
            isPermissionGranted = true
            loadMockData(LatLng(userLat, userLng))
        } else {
            // 권한이 없으면 팝업 요청
            launcher.launch(PermissionManager.locationPermissions)
        }
    }

    // --- [전체 레이아웃 구성] ---
    Box(modifier = Modifier.fillMaxSize()) {

        // [레이어 1] 구글 지도 영역
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // 권한 허용 시 지도에 내 위치 파란 점 표시
            properties = MapProperties(isMyLocationEnabled = isPermissionGranted),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false, // 시안의 커스텀 버튼을 쓰기 위해 기본 버튼 비활성화
                zoomControlsEnabled = false // UI 깔끔함을 위해 줌 버튼 숨김
            )
        ) {
            // 목업 데이터 10개를 순회하며 지도에 마커 배치 (F-MAP-04)
            mockNotes.forEach { note ->
                // 내 위치(userLat/Lng)와 쪽지 위치 간의 실제 거리 계산
                val distance = LocationUtil.calculateDistance(userLat, userLng, note.lat, note.lng)
                // 30m 이내인지 여부 판단 (F-MAP-05)
                val isPickable = distance <= 30f

                // key()를 사용해 Compose 재랜더링 시 마커의 고유성 보장 (에러 방지용)
                key(note.id) {
                    MarkerComposable(
                        state = MarkerState(position = LatLng(note.lat, note.lng))
                    ) {
                        // 거리 조건에 따라 색상 결정: 30m 안 = 주황색, 밖 = 회색
                        val color = if (isPickable) JoopJoopOrange else Color.Gray
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, CircleShape)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 현재 ic_note 리소스가 없으므로 이메일 아이콘으로 대체함
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
                    // 버튼 클릭 시 현재 지도의 중심 좌표를 기준으로 가짜 데이터를 새로 뿌림
                    loadMockData(cameraPositionState.position.target) },

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

            // 주변 쪽지 요약 정보 카드 (F-MAP-08)
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
                        Text(
                            "${mockNotes.size} new notes nearby",
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