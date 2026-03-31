package com.example.joopjoop.feature.note.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.common.util.ImageProcessor
import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.data.location.LocationProvider
import com.example.joopjoop.feature.note.ui.write.WriteNoteUiState
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class WriteNoteViewModel(
    private val repository: NoteRepository,
//    private val fusedLocationClient: FusedLocationProviderClient
    private val authRepository: AuthRepository,
    private val locationProvider: LocationProvider // [변경] 공통 Provider 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteNoteUiState())
    val uiState: StateFlow<WriteNoteUiState> = _uiState.asStateFlow()

    val categories = listOf("일상", "감성", "추억", "맛집")
    private val timeOptions = listOf(3, 6, 12, 24)

    init {
        fetchCurrentLocation()
        initUserInfo()
    }

//    private fun fetchCurrentLocation() {
//        try {
//            // 사용자 마지막 위치
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                if (location != null) {
//                    updateLocationState(location)
//                } else {
//                    // 마지막 위치가 없으면 새로 요청 (빠른 화면전환이거나 외부 상황으로 위치 값이 못가져온 상태일 수도 있기에)
//                    fusedLocationClient.getCurrentLocation(
//                        Priority.PRIORITY_HIGH_ACCURACY,
//                        CancellationTokenSource().token
//                    ).addOnSuccessListener { newLocation ->
//                        newLocation?.let { updateLocationState(it) }
//                    }
//                }
//            }.addOnFailureListener {
//                Log.e("WriteNoteViewModel", "위치 가져오기 실패: ${it.message}")
//            }
//        } catch (e: SecurityException) {
//        }
//    }

    /**
     * [이전 작업자 참고]
     * 기존의 fusedLocationClient 콜백 방식 대신, 공통 유틸인 LocationProvider를 사용
     * 초기 진입 시 현재 위치를 한 번 가져와서 상태를 업데이트
     */

    // 위치 가져오기
    private fun fetchCurrentLocation() {
        viewModelScope.launch {
            try {
                // LocationProvider 내부에서 getCurrentLocation(Priority_HIGH) 로직이 처리됨
                val location = locationProvider.getCurrentLocation()
                location?.let { updateLocationState(it) }
            } catch (e: Exception) {
                Log.e("WriteNoteViewModel", "초기 위치 가져오기 실패: ${e.message}")
            }
        }
    }

    // 유저 정보 업데이트
    private fun initUserInfo() {
        viewModelScope.launch {
            authRepository.currentUser
                .collect { user ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            user = currentState.user.copy(
                                uid = user?.uid ?: "",
                                nickname = user?.nickname ?: "익명사용자"
                            )
                        )
                    }
                }
        }
    }

    // GeoHash 기반 위치 검색 및 uiState 저장
    private fun updateLocationState(location: Location) {
        val hash = GeoFireUtils.getGeoHashForLocation(
            GeoLocation(location.latitude, location.longitude)
        )
        _uiState.update { state ->
            state.copy(
                location = state.location.copy(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    geohash = hash
                )
            )
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onContentChange(content: String) {
        if (content.length <= 300) {
            _uiState.update { it.copy(noteContent = content) }
        }
    }

    fun increaseHours() {
        val currentIndex = timeOptions.indexOf(_uiState.value.storageHours)
        if (currentIndex < timeOptions.size - 1) {
            _uiState.update { it.copy(storageHours = timeOptions[currentIndex + 1]) }
        }
    }

    fun decreaseHours() {
        val currentIndex = timeOptions.indexOf(_uiState.value.storageHours)
        if (currentIndex > 0) {
            _uiState.update { it.copy(storageHours = timeOptions[currentIndex - 1]) }
        }
    }

    fun onImageSelected(uri: Uri, context: Context) {
        _uiState.update {
            it.copy(
                selectedImageUri = uri.toString(),
                isImageUploading = true
            )
        }

        viewModelScope.launch {
            try {
                // 1. ImageProcessor를 통해 원본과 썸네일 '둘 다' 생성
                val (originalData, thumbData) = withContext(Dispatchers.IO) {
                    val processor = ImageProcessor(context)
                    val original = processor.processOriginal(uri)
                    val thumb = processor.processThumbnail(uri)
                    original to thumb
                }

                if (originalData == null || thumbData == null) {
                    _uiState.update {
                        it.copy(
                            isImageUploading = false,
                            errorMessage = "이미지 가공 실패"
                        )
                    }
                    return@launch
                }

                val timestamp = System.currentTimeMillis()

                // 2. 원본 이미지 업로드
                val originalUrl = repository.uploadImage(
                    originalData,
                    "note_$timestamp",
                    onProgress = { progress ->
                        _uiState.update { it.copy(uploadProgress = progress) }
                    })

                // 3. 썸네일 이미지 업로드 (파일명에 _thumb 추가)
                val thumbUrl =
                    repository.uploadImage(thumbData, "note_${timestamp}_thumb", onProgress = {})

                if (originalUrl != null && thumbUrl != null) {
                    // 4. 업로드 성공 시, 원본 URL과 썸네일 URL을 모두 상태에 저장
                    _uiState.update {
                        it.copy(
                            selectedImageUri = originalUrl,    // 원본 URL
                            selectedThumbnailUri = thumbUrl,   // 썸네일 URL
                            isImageUploading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isImageUploading = false, errorMessage = "서버 전송 실패") }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isImageUploading = false,
                        errorMessage = "오류 발생: ${e.message}"
                    )
                }
            }
        }

        /*
                            // ImageProcessor를 통해 리사이징 및 압축
                            val processedData = withContext(Dispatchers.IO) {
                                ImageProcessor(context).processOriginal(uri)
                            }

                            if (processedData == null) {
                                _uiState.update { it.copy(isImageUploading = false, errorMessage = "이미지 가공 실패") }
                                return@launch
                            }

                            Log.d("es", "이미지 가공 성공: 크기 ${processedData.size} bytes")

                            // 가공된 ByteArray 데이터와 파일명을 Repository에 전달
                            val fileName = "note_${System.currentTimeMillis()}"
                            val imageUrl = repository.uploadImage(processedData, fileName, onProgress = { progress ->
                                _uiState.update { it.copy(uploadProgress = progress) }
                            })

                            if (imageUrl != null) {
                                // 업로드 성공 시, 상태의 URI를 '서버 URL(https://)'로 교체
                                _uiState.update { it.copy(
                                    selectedImageUri = imageUrl,
                                    isImageUploading = false
                                ) }
                            } else {
                                _uiState.update { it.copy(isImageUploading = false, errorMessage = "서버 전송 실패") }
                            }
                        } catch (e: Exception) {
                            _uiState.update { it.copy(isImageUploading = false, errorMessage = "오류 발생: ${e.message}") }
                        }
                    }
                }*/
    }

    fun onImageRemoved() {
        _uiState.update {
            it.copy(
                selectedImageUri = null,
                selectedThumbnailUri = null
            )
        }
    }

    val isSubmitEnabled: Boolean
        get() = _uiState.value.noteContent.isNotBlank() && !_uiState.value.isSubmitting

//    @SuppressLint("MissingPermission")
//    fun submitNote(context: Context) {
//        val currentState = _uiState.value
//        if (currentState.noteContent.isBlank() || currentState.isSubmitting) return
//
//        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
//
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            if (location != null && location.latitude != 0.0) {
//                performSubmit(context, location.latitude, location.longitude)
//            } else {
//                // 위치를 못 잡았다면 실시간 위치 요청
//                val priority = Priority.PRIORITY_HIGH_ACCURACY
//                fusedLocationClient.getCurrentLocation(priority, null)
//                    .addOnSuccessListener { curLoc ->
//                        if (curLoc != null) {
//                            performSubmit(context, curLoc.latitude, curLoc.longitude)
//                        } else {
//                            _uiState.update {
//                                it.copy(
//                                    isSubmitting = false,
//                                    errorMessage = "위치 정보를 가져올 수 없습니다. GPS를 켜주세요."
//                                )
//                            }
//                        }
//                    }
//            }
//        }.addOnFailureListener {
//            _uiState.update { it.copy(isSubmitting = false, errorMessage = "위치 획득 실패") }
//        }
//    }

    /**
     * [이전 작업자 참고]
     * 제출 시점에 최신 위치를 확인하기 위해 locationProvider를 활용합니다.
     * 기존의 중첩된 addOnSuccessListener(콜백) 구조를 제거하고 순차적인 코루틴 흐름으로 변경했습니다.
     */
    fun submitNote(context: Context) {
        val currentState = _uiState.value
        if (currentState.noteContent.isBlank() || currentState.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // 1. 최신 위치 정보 획득 (콜백 대신 suspend 함수 사용)
                val location = locationProvider.getCurrentLocation()

                if (location != null && location.latitude != 0.0) {
                    // 2. 위치 획득 성공 시 기존 제출 로직(performSubmit) 실행
                    performSubmit(context, location.latitude, location.longitude)
                } else {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = "위치 정보를 가져올 수 없습니다. GPS를 확인해주세요."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSubmitting = false, errorMessage = "위치 획득 실패: ${e.message}")
                }
            }
        }
    }

    fun performSubmit(context: Context, lat: Double, lng: Double) {
        if (lat == 0.0 || lng == 0.0) {
            return
        }
        val currentState = _uiState.value

        if (currentState.isImageUploading) {
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    errorMessage = "사진 업로드 중입니다. 잠시만 기다려주세요."
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                // 좌표 기반 "구 동" 텍스트 추출
                val addressDisplay = getAddress(context, lat, lng)

                // Geohash 생성
                val hash = LocationUtil.getGeohash(lat, lng)

                // 작성 현재 시간 및 노출 시간 계산한 실제 시간
                val currentTime = System.currentTimeMillis()
                val expiresTime = currentTime + (currentState.storageHours * 3600000L)

                val location = NoteLocation(
                    latitude = lat,
                    longitude = lng,
                    address = addressDisplay,
                    geohash = hash
                )

                val request = Note(
                    authorId = currentState.user.uid,
                    userNickname = currentState.user.nickname,
                    contentText = currentState.noteContent,
                    category = currentState.selectedCategory,
                    imageUrl = currentState.selectedImageUri,
                    thumbnailUrl = currentState.selectedThumbnailUri,
                    createdAt = Date(currentTime),
                    expiresAt = Date(expiresTime),
                    location = location
                )

                val newId = repository.createNote(request)
                _uiState.update {
                    it.copy(
                        isSubmitSuccess = true,
                        createdNoteId = newId,
                        isSubmitting = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSubmitting = false, errorMessage = "저장 실패: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetNote() {
        _uiState.update { WriteNoteUiState() }
    }

    private suspend fun getAddress(
        context: Context,
        lat: Double,
        lng: Double
    ): String = suspendCancellableCoroutine { continuation ->
        val geocoder = Geocoder(context, Locale.KOREA)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    val result = getGuDong(addresses.firstOrNull())
                    continuation.resume(result)
                }
            } else {
                @Suppress("DEPRECATION")
                val address = geocoder.getFromLocation(lat, lng, 1)?.firstOrNull()
                continuation.resume(getGuDong(address))
            }
        } catch (e: Exception) {
            continuation.resume("쪽지 위치")
        }
    }

    fun getGuDong(address: Address?): String {
        if (address == null) return "쪽지 위치"

        // 전체 주소
        val fullAddress = address.getAddressLine(0) ?: return "쪽지 위치"

        // 공백 나눔
        val parts = fullAddress.split(" ")

        var gu = ""
        var dong = ""

        // 뒤에서부터 검색하여 가장 먼저 나오는 동과 구 글자를 찾음
        for (i in parts.lastIndex downTo 0) {
            val part = parts[i]
            if (dong.isEmpty() && (part.endsWith("동") || part.endsWith("가") || part.endsWith("로"))) {
                dong = part
            } else if (gu.isEmpty() && part.endsWith("구")) {
                gu = part
            }

            // 둘 다 찾았으면 중단
            if (gu.isNotEmpty() && dong.isNotEmpty()) break
        }

        return when {
            gu.isNotEmpty() && dong.isNotEmpty() -> "$gu $dong"
            gu.isNotEmpty() -> gu // 구만 있을 때
            dong.isNotEmpty() -> dong // 동만 있을 때
            else -> address.subLocality ?: address.locality ?: "쪽지 위치" // 둘 다 없다면
        }
    }
}