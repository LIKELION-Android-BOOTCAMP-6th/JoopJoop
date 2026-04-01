package com.example.joopjoop.core.common.util


import android.location.Location
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng

object LocationUtil {

//    // 원래 사용자의 마지막 위치를 가져오는 용도로 작성되었으나
//    // 'LocationProvider(DI 주입)'를 사용하는 방식으로 대체
//    // 1. 자원 낭비: 호출 시마다 FusedLocationProviderClient를 새로 생성함.
//    // 2. 단일 책임 원칙: Util은 '계산'만 담당하고, '위치 획득'은 상태를 가진 Provider가 담당하도록 역할 분리.
//
//    @SuppressLint("MissingPermission") // 호출 전 권한 체크가 완료되었다고 가정.
//    fun getLastLocation(context: Context, onLocationReceived: (Location) -> Unit) {
//        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//
//        // 시스템에 기록된 마지막 위치 요청
//        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//            if (location != null) {
//                onLocationReceived(location) // 성공적으로 가져오면 콜백 실행
//            } else {
//                // 위치가 null일 경우(GPS가 꺼진 직후 등)에 대한 처리가 필요.
//            }
//        }
//    }

    // 두 지점 간의 직선 거리(미터)를 계산합니다. (진짜 원형 반경 계산을 위해)
    fun calculateDistance(
        startLat: Double, startLng: Double,
        endLat: Double, endLng: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLat, startLng, endLat, endLng, results)
        return results[0]
    }

    // Geohash 생성 (Firestore 쿼리용)
    fun getGeohash(lat: Double, lng: Double): String {
        // GeoFire 라이브러리 사용
        return GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lng))
    }

    // 쪽지 쿼리시 거리계산해서 Text값 저장하기 위해
    fun getDistanceText(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): String {
        val results = FloatArray(1)
        Location.distanceBetween(
            lat1, lng1, lat2, lng2, results
        )
        val distance = results[0]

        return if (distance < 1000) {
            "${distance.toInt()}m"
        } else {
            String.format("%.1fkm", distance / 1000)
        }
    }

    // geohash 격자 출력을 위해서 추가
    fun getGeohashBounds(geohash: String): List<LatLng> {
        if (geohash.isEmpty()) return emptyList()

        return try {
            val BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"
            var isEven = true

            // doubleArrayOf 에러 방지를 위해 명시적 생성
            val latRange = doubleArrayOf(-90.0, 90.0)
            val lonRange = doubleArrayOf(-180.0, 180.0)

            for (char in geohash) {
                val cd = BASE32.indexOf(char)
                if (cd == -1) continue // 잘못된 문자 스킵

                for (j in 4 downTo 0) {
                    val mask = 1 shl j
                    if (isEven) {
                        // 경도(Longitude) 범위 좁히기
                        val mid = (lonRange[0] + lonRange[1]) / 2
                        if ((cd and mask) != 0) lonRange[0] = mid else lonRange[1] = mid
                    } else {
                        // 위도(Latitude) 범위 좁히기
                        val mid = (latRange[0] + latRange[1]) / 2
                        if ((cd and mask) != 0) latRange[0] = mid else latRange[1] = mid
                    }
                    isEven = !isEven
                }
            }

            // 사각형의 네 꼭짓점 반환
            listOf(
                LatLng(latRange[0], lonRange[0]), // 남서
                LatLng(latRange[1], lonRange[0]), // 북서
                LatLng(latRange[1], lonRange[1]), // 북동
                LatLng(latRange[0], lonRange[1]), // 남동
                LatLng(latRange[0], lonRange[0])  // 닫기 (Polygon 연결용)
            )
        } catch (e: Exception) {
            emptyList()
        }
    }
}