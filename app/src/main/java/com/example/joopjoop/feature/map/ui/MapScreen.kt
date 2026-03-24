package com.example.joopjoop.feature.map.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapMainScreen() {
    // 1. 초기 카메라 위치 설정 (서울 시청)
    val seoulCityHall = LatLng(37.5665, 126.9780)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(seoulCityHall, 15f)
    }

    // 2. 구글 맵 컴포넌트 호출
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // 3. 지도 위에 테스트 마커 찍기
        Marker(
            state = MarkerState(position = seoulCityHall),
            title = "JoopJoop 테스트 지점",
            snippet = "지도가 정상적으로 로드되었습니다!"
        )
    }
}