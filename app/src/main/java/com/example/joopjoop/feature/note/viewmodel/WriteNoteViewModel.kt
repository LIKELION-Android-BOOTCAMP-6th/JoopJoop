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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume

class WriteNoteViewModel(
    private val repository: NoteRepository,
//    private val fusedLocationClient: FusedLocationProviderClient
    private val authRepository: AuthRepository,
    private val locationProvider: LocationProvider // [변경] 공통 Provider 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteNoteUiState())
    val uiState: StateFlow<WriteNoteUiState> = _uiState.asStateFlow()
    private var editingNoteId: String? = null   // 쪽지 수정하기 위한 노트 id

    val categories = listOf("일상", "감성", "추억", "맛집")
    private val timeOptions = listOf(3, 6, 12, 24)

    init {
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
        Log.d("jay", "editedNoteId ============== ${_uiState.value.editedNoteId}")
        if (_uiState.value.editedNoteId != null) return

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
                    Log.d("WriteNoteDebug", """
                ┌────────────────────────────────────────────────────
                │ 👤 현재 로그인 유저 정보 체크 (Auth)
                ├────────────────────────────────────────────────────
                │ 🆔 UID: ${user?.uid}
                │ 🏷️ 닉네임: ${user?.nickname}
                │ 🖼️ 이미지URL: '${user?.profileImageUrl}'  <-- 이게 비어있으면 저장도 비어서 됩니다!
                └────────────────────────────────────────────────────
            """.trimIndent())
                    _uiState.update { currentState ->
                        currentState.copy(
                            user = currentState.user.copy(
                                uid = user?.uid ?: "",
                                nickname = user?.nickname ?: "익명사용자",
                                profileImageUrl = user?.profileImageUrl ?: ""
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
                isImageUploading = true,
                uploadProgress = 0f
            )
        }

        viewModelScope.launch {
            try {
                // 1. 이미지 가공 (원본과 썸네일 ByteArray 생성)
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
                val fileName = "note_$timestamp"

                // 2. 수정된 Repository 함수 호출 (원본/썸네일 동시 업로드)
                val urls = repository.uploadImage(
                    originalData = originalData,
                    thumbnailData = thumbData,
                    fileName = fileName,
                    onProgress = { progress ->
                        // 원본 업로드 진행률을 UI에 반영
                        _uiState.update { it.copy(uploadProgress = progress) }
                    }
                )

                if (urls != null) {
                    // 3. 반환받은 Pair에서 원본(first)과 썸네일(second) URL 추출
                    val (originalUrl, thumbnailUrl) = urls

                    _uiState.update {
                        it.copy(
                            selectedImageUri = originalUrl,    // 원본 URL 저장
                            selectedThumbnailUri = thumbnailUrl, // 썸네일 URL 저장
                            isImageUploading = false,
                            uploadProgress = 1f
                        )
                    }
                    Log.d("PhotoDebug", "업로드 성공 - 원본: $originalUrl, 썸네일: $thumbnailUrl")
                } else {
                    _uiState.update { it.copy(isImageUploading = false, errorMessage = "서버 전송 실패") }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isImageUploading = false, errorMessage = "오류 발생: ${e.message}")
                }
            }
        }
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
        if (lat == 0.0 || lng == 0.0) return

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
                // 수정 작업 분기 처리
                val isEditMode = currentState.editedNoteId != null
                val currentState = _uiState.value

                // 1. 공통으로 사용할 변수 선언 (초기값 설정 또는 선언만 수행)
                val finalLocation: NoteLocation
                val finalCreatedAt: Date
                val finalExpiresAt: Date
                val id: String
                val finalStorageHours : Int

                if (isEditMode) {
                    // [쪽지 수정] 기존 uiState에 저장된 값을 그대로 사용한다
                    finalLocation = currentState.location
                    finalCreatedAt = currentState.createdAt
                    finalStorageHours = currentState.storageHours
                    finalExpiresAt = Date(finalCreatedAt.time + (finalStorageHours * 3600000L))
                    id = currentState.editedNoteId.toString()
                } else {
                    // 새로 쪽지 작성
                    val currentTime = System.currentTimeMillis()
                    val addressDisplay = getAddress(context, lat, lng)
                    val hash = LocationUtil.getGeohash(lat, lng)

                    id = ""
                    finalStorageHours = currentState.storageHours
                    finalCreatedAt = Date(currentTime)           // 작성 현재 시간
                    finalExpiresAt =
                        Date(currentTime + (currentState.storageHours * 3600000L))      // 노출 시간 (실제 시간)

                    finalLocation = NoteLocation(
                        latitude = lat,
                        longitude = lng,
                        address = addressDisplay,
                        geohash = hash
                    )
                }

                val request = Note(
                    id = currentState.editedNoteId.toString(),
                    authorId = currentState.user.uid,
                    userNickname = currentState.user.nickname,
                    profileImageUrl = currentState.user.profileImageUrl,
                    contentText = currentState.noteContent,
                    category = currentState.selectedCategory,
                    imageUrl = currentState.selectedImageUri,
                    thumbnailUrl = currentState.selectedThumbnailUri,
                    storageHours = finalStorageHours,
                    createdAt = finalCreatedAt,
                    expiresAt = finalExpiresAt,
                    location = finalLocation
                )

                if (isEditMode) {
                    // 쪽지 수정
                    repository.submitEditedNote(currentState.editedNoteId!!, request)
                } else {
                    // 새 쪽지 작성
                    repository.createNote(request)
                }

                _uiState.update {
                    it.copy(isSubmitting = false, isSubmitSuccess = true)
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

    // 쪽지 수정 하기 위한 데이터 로딩
    fun loadNoteForEdit(noteId: String?) {
        if (noteId == null) {
            editingNoteId = null
            return
        }

        editingNoteId = noteId

        viewModelScope.launch {
            try {
                val note = repository.getNoteDetail(noteId)
                note?.let { data ->
                    _uiState.update {
                        it.copy(
                            editedNoteId = noteId,           // 수정할 쪽지 ID 저장
                            noteContent = data.contentText,   // 기존 내용
                            selectedCategory = data.category, // 기존 카테고리
                            selectedImageUri = data.imageUrl, // 기존 이미지
                            selectedThumbnailUri = data.thumbnailUrl, // 썸네일 이미지
                            location = data.location,         // 기존 위치(좌표, 주소, 해시)
                            storageHours = data.storageHours, // 쪽지 보관 시간
                            createdAt = data.createdAt,       // 최초 작성일
                            expiresAt = data.expiresAt        // 만료 예정일
                        )
                    }
                    Log.d("jay", "contentText = ${data.contentText}")
                    Log.d("jay", "selectedCategory = ${data.category}")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "데이터를 불러오지 못했습니다.") }
            }
        }
    }

    // 작성 페이지 노출했을 때 호출
    fun prepareNewNote() {
        // 이미 쪽지 id가 있으면 위치 잡지 않음 (수정상태일때는 위치 검색 x)
        if (_uiState.value.editedNoteId != null) return
        fetchCurrentLocation()
    }
}