package com.example.joopjoop.feature.map.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.joopjoop.core.common.util.PermissionManager
import com.example.joopjoop.data.location.LocationProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun MapMainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. 도구들 준비
    val locationProvider = remember { LocationProvider(context) }
    val cameraPositionState = rememberCameraPositionState()

    // 2. 권한 런처 (지도가 켜질 때 바로 요청하거나 버튼 클릭 시 사용)
    val launcher = PermissionManager.rememberLocationPermissionLauncher { isGranted ->
        if (isGranted) {
            // 권한 허용 시 내 위치로 카메라 이동
            scope.launch {
                val loc = locationProvider.getCurrentLocation()
                loc?.let {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                    )
                }
            }
        }
    }

    // 3. 화면 진입 시 권한 체크 후 카메라 이동
    LaunchedEffect(Unit) {
        if (PermissionManager.hasLocationPermission(context)) {
            val loc = locationProvider.getCurrentLocation()
            loc?.let {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
            }
        } else {
            launcher.launch(PermissionManager.locationPermissions)
        }
    }

    // 4. 실제 지도 UI
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            // ★ 핵심: 권한이 있을 때만 내 위치 파란 점 활성화
            isMyLocationEnabled = PermissionManager.hasLocationPermission(context)
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true // 내 위치로 이동하는 버튼 활성화
        )
    )
}