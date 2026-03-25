package com.example.joopjoop.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationProvider(context: Context) {

    // 구글 위치 서비스 클라이언트 초기화
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * 현재 위치를 1회성으로 가져오는 함수 (Coroutines 사용)
     * 권한 체크는 호출하는 쪽(ViewModel 또는 Activity)에서 미리 해야함.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        // [비동기 콜백을 코루틴의 순차적 리턴 방식으로 변환하는 브릿지]
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location: Location? ->
                // 작업 성공 시 결과값을 들고 다시 깨어남 (Resume)
                continuation.resume(location)
            }.addOnFailureListener {
                // 실패 시 null을 반환하며 깨어남
                continuation.resume(null)
            }
        }
    }
}