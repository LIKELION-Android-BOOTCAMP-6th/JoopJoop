package com.example.joopjoop.core.common.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

object PermissionManager {

    // 1. 요청할 위치 권한 세트 (정밀도 + 대략적 위치)
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /**
     * 2. 현재 권한 상태를 단순 체크하는 함수
     * 버튼 클릭 시점에 "이미 권한이 있는가?"를 판단할 때 씁니다.
     */
    fun hasLocationPermission(context: Context): Boolean {
        return locationPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 3. Compose UI에서 권한 팝업을 띄울 '도구(Launcher)'를 만드는 함수
     * @param onPermissionResult 권한 결과 처리 로직 (허용 시 true, 거부 시 false)
     */
    @Composable
    fun rememberLocationPermissionLauncher(
        onPermissionResult: (Boolean) -> Unit
    ) = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 모든 요청 권한이 승인되었는지 확인
        val isAllGranted = permissions.values.all { it }
        onPermissionResult(isAllGranted)
    }
}