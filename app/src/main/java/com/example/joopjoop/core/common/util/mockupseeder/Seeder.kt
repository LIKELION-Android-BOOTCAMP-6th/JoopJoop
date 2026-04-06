package com.example.joopjoop.core.common.util.mockupseeder

import android.content.Context
import android.util.Log
import com.example.joopjoop.BuildConfig
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import com.firebase.geofire.core.GeoHash
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random


// 테스트와 데모데이를 위한 쪽지 시딩(Seeding) 클래스
// Gemini AI를 활용해 쪽지를 생성하여 fireStore에 문서를 저장함
// local.properties 파일에
// GEMINI_API_KEY= 실제 KEY를 넣어줘야함

class Seeder {
    private val db = Firebase.firestore
    private val tag = "Seeder"

    // Gemini 1.5 Flash 모델 설정 (local.properties의 API KEY 사용)
    private val generativeModel = GenerativeModel(
        modelName = "gemini-flash-latest",
        apiKey = BuildConfig.GEMINI_API_KEY
    )


    // 서울 전역 균형 배포를 위한 주요 거점 데이터
    private val seoulBalancedHotspots = mapOf(

        // 서울 CORE (겹침 허용)
        "강남역" to LatLng(37.4979, 127.0276),
        "선릉" to LatLng(37.5045, 127.0490),
        "잠실" to LatLng(37.5090, 127.1009),
        "홍대입구" to LatLng(37.5575, 126.9245),
        "합정" to LatLng(37.5496, 126.9137),

        // 서울 고밀도
        "성수동" to LatLng(37.5445, 127.0560),
        "광화문" to LatLng(37.5704, 126.9772),
        "이태원" to LatLng(37.5345, 126.9945),
        "여의도" to LatLng(37.5262, 126.9333),

        // 서울 중밀도
        "영등포" to LatLng(37.5157, 126.9073),
        "동대문" to LatLng(37.5744, 127.0396),
        "건대입구" to LatLng(37.5410, 127.0710),
        "노량진" to LatLng(37.5134, 126.9427),

        // 서울 커버
        "은평" to LatLng(37.6176, 126.9227),
        "마곡" to LatLng(37.5608, 126.8255),
        "관악" to LatLng(37.4784, 126.9516),
        "중랑" to LatLng(37.5950, 127.0920),
        "성북" to LatLng(37.5894, 127.0167),

        // 서울 외곽
        "노원" to LatLng(37.6562, 127.0632),
        "강동" to LatLng(37.5386, 127.1233),

        // 경기 CORE (서울 생활권 핵심)
        "판교" to LatLng(37.3947, 127.1112),
        "분당" to LatLng(37.3786, 127.1126),
        "수원역" to LatLng(37.2663, 127.0007),

        // 경기 서부
        "부천" to LatLng(37.5034, 126.7660),
        "인천 구월동" to LatLng(37.4516, 126.7010),

        // 경기 북서
        "일산" to LatLng(37.6584, 126.7702),
        "파주 운정" to LatLng(37.7120, 126.7530),

        // 경기 남부
        "용인 수지" to LatLng(37.3222, 127.0970),
        "동탄" to LatLng(37.2009, 127.0950),

        // 경기 동부
        "구리" to LatLng(37.6033, 127.1396),
        "남양주" to LatLng(37.6360, 127.2165)
    )

    private val categories = listOf("일상", "감성", "추억", "맛집")


    /*메인 실행 함수: 서울 전체 거점에 목업 데이터를 배포*/
    suspend fun seedFullSeoul(
        context: Context,
        cycle: Int = 1,
        onProgress: (Int, Int) -> Unit = { _, _ -> } // 콜백 추가 (현재 인덱스, 전체 개수)
    ) = withContext(Dispatchers.IO) {
        Log.d(tag, "리얼 시딩 시작...")

        // Gemini를 통해 가상의 유저 50명의 닉네임 생성
        val mockUsers = fetchRealMockUsers(50)
        if (mockUsers.isEmpty()) return@withContext

        // 설정된 거점을 순회하며 데이터 생성
        val hotspots = seoulBalancedHotspots.toList()
        val total = hotspots.size * cycle

        var currentCount = 0

        var batch = db.batch()
        var batchCount = 0

        repeat(cycle) {

            hotspots.forEach { pair ->
                val regionName = pair.first
                val baseCoords = pair.second

                try {
                    val message = fetchMessages(1).firstOrNull()
                        ?: "오늘은 그냥 그런 하루다"

                    val user = mockUsers.random()
                    val noteId = "mock_${UUID.randomUUID()}"
                    val randomPos = getRandomLocation(baseCoords)

                    val geoHash = GeoHash(randomPos.latitude, randomPos.longitude).geoHashString
                    val friendlyAddress = getAddressFromLatLng(context, randomPos)

                    val storageHours = listOf(3, 6, 9, 12, 24).random()
                    val createdAt = getRandomCreatedAt(storageHours)
                    val expiresAt = Date(createdAt.time + (3600000 * storageHours))

                    val randomImg = if (Random.nextFloat() < 0.6f)
                        "https://picsum.photos/seed/${UUID.randomUUID()}/600/800" else null

                    val note = Note(
                        id = noteId,
                        authorId = "mock_${user.nickname}",
                        userNickname = user.nickname,
                        profileImageUrl = user.profileUrl,
                        contentText = message,
                        thumbnailUrl = randomImg,
                        imageUrl = randomImg,
                        category = categories.random(),
                        viewCount = Random.nextInt(0, 100),
                        likeCount = Random.nextInt(0, 20),
                        location = NoteLocation(
                            geohash = geoHash,
                            latitude = randomPos.latitude,
                            longitude = randomPos.longitude,
                            address = friendlyAddress,
                            distance = ""
                        ),
                        isActive = true,
                        storageHours = storageHours,
                        createdAt = createdAt,
                        expiresAt = expiresAt
                    )

                    val docRef = db.collection("notes").document(noteId)
                    batch.set(docRef, note)
                    batchCount++

                    // 30개마다 commit
                    if (batchCount == 30) {
                        batch.commit().await()
                        batch = db.batch()
                        batchCount = 0
                    }

                    currentCount++
                    withContext(Dispatchers.Main) {
                        onProgress(currentCount, total)
                    }

                } catch (e: Exception) {
                    Log.e(tag, "$regionName 에러: ${e.message}")
                }
            }
        }

        if (batchCount > 0) {
            batch.commit().await()
        }

        // 모든 지역 시딩 완료 후 호출
        withContext(Dispatchers.Main) {
            onProgress(total, total)
        }
    }


    // Gemini를 호출하여 가짜 유저 정보를 생성
    private suspend fun fetchRealMockUsers(count: Int): List<MockUser> {
        // 프롬프트를 더 구체적으로 수정 (설명 없이 이름만 나오게)
        val prompt = """
                한국어 닉네임 ${count}개를 생성해.
                
                조건:
                - 반드시 닉네임만 출력
                - 각 줄에 하나씩 출력
                - 설명, 해설, 문장, 부가 텍스트 절대 금지
                - 따옴표, 번호, 기호 사용 금지
                - 한글 또는 영어+숫자만 사용
                - 2~10자 사이
                
                출력 예시:
                달빛산책자
                커피중독자
                냥냥펀치
                하루한잔
                """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            val names = response.text?.split("\n")
                ?.map { it.trim().replace(Regex("[^가-힣a-zA-Z0-9]"), "") } // 특수문자/번호 제거
                ?.filter { it.isNotBlank() } ?: emptyList()

            Log.d(tag, "생성된 닉네임들: $names") // 로그 추가해서 확인

            names.mapIndexed { i, name ->
                MockUser(name, "https://i.pravatar.cc/150?u=$i")
            }
        } catch (e: Exception) {
            Log.e(tag, "유저 생성 에러: ${e.message}")
            emptyList()
        }
    }


    // Gemini를 호출하여 쪽지 내용을 생성
    private suspend fun fetchMessages(count: Int): List<String> {
        val prompt = """
                일상에서 누구나 공감할 수 있는 짧은 한 문장을 ${count}개 만들어줘.
    
                조건:
                - 10~30자 내외
                - 자연스러운 구어체
                - SNS 느낌
                - 각 줄은 하나의 쪽지
                - 반드시 쪽지 내용만 출력
                - 설명, 제목, Tip, 부가 설명 절대 금지
                - 특수 기호 (***, **, # 등) 사용 금지
                - 줄바꿈으로만 구분
    
                예시:
                오늘 날씨 너무 좋다 ☀️
                퇴근길에 커피 한 잔 ☕
            """.trimIndent()
        val response = generativeModel.generateContent(prompt)

        return response.text
            ?.split("\n")
            ?.map { it.trim() }
            ?.filter {
                it.isNotBlank() &&
                        !it.matches(Regex("^\\d+\\..*")) &&
                        !it.contains("Tip") &&
                        !it.contains("설명") &&
                        !it.contains("***") &&
                        !it.contains("**") &&
                        it.length in 5..50 // 너무 긴 설명 제거
            }
            ?: emptyList()
    }

    /**
     * 기준 좌표에서 약 5km 반경 내로 위치를 랜덤하게 균등 분산시킵니다.
     */
    private fun getRandomLocation(base: LatLng, radiusKm: Double = 2.5): LatLng {
        val radiusInDegrees = radiusKm / 111.0
        val u = Random.nextDouble()
        val v = Random.nextDouble()
        val w = radiusInDegrees * Math.sqrt(u)
        val t = 2 * Math.PI * v
        val latOffset = w * Math.cos(t)
        val lngOffset = w * Math.sin(t) / Math.cos(Math.toRadians(base.latitude))
        return LatLng(
            base.latitude + latOffset,
            base.longitude + lngOffset
        )
    }

    // 임시 유저 데이터를 담기 위한 내부 데이터 클래스
    data class MockUser(val nickname: String, val profileUrl: String)

    // 랜덤 생성 시간
    private fun getRandomCreatedAt(storageHours: Int): Date {
        val now = System.currentTimeMillis()

        val maxMinutes = storageHours * 60

        val randomMinutesAgo = Random.nextLong(0, maxMinutes.toLong())

        return Date(now - randomMinutesAgo * 60 * 1000)
    }

    // Geocoder를 사용하여 좌표를 주소 문자열로 변환하는 유틸 함수
    private fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
        val geocoder = android.location.Geocoder(context, Locale.KOREA)

        return try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                val gu = address.subAdminArea
                val dong = address.subLocality
                    ?: address.thoroughfare   // fallback (OO로)
                    ?: address.featureName    // fallback (건물명 등)

                when {
                    !gu.isNullOrBlank() && !dong.isNullOrBlank() -> "$gu $dong"
                    !gu.isNullOrBlank() -> "$gu 인근"
                    !dong.isNullOrBlank() -> "$dong 인근"
                    else -> "서울 어딘가"
                }

            } else {
                "서울 어딘가"
            }

        } catch (e: Exception) {
            "서울 어딘가"
        }
    }
}