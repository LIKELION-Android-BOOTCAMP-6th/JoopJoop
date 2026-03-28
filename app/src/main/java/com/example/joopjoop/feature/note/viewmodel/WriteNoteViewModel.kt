package com.example.joopjoop.feature.note.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.note.data.model.NoteRequest
import com.example.joopjoop.feature.note.ui.write.WriteNoteUiState
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class WriteNoteViewModel(
    private val repository: NoteRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteNoteUiState())
    val uiState: StateFlow<WriteNoteUiState> = _uiState.asStateFlow()

    val categories = listOf("일상", "감성", "추억", "맛집")
    private val timeOptions = listOf(3, 6, 12, 24)

    init {
        fetchCurrentLocation()
    }

    private fun fetchCurrentLocation() {
        try {
            // 사용자 마지막 위치
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    updateLocationState(location)
                } else {
                    // 마지막 위치가 없으면 새로 요청 (빠른 화면전환이거나 외부 상황으로 위치 값이 못가져온 상태일 수도 있기에)
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).addOnSuccessListener { newLocation ->
                        newLocation?.let { updateLocationState(it) }
                    }
                }
            }.addOnFailureListener {
                Log.e("WriteNoteViewModel", "위치 가져오기 실패: ${it.message}")
            }
        } catch (e: SecurityException) {
        }
    }

    // GeoHash 기반 위치 검색 및 uiState 저장
    private fun updateLocationState(location: Location) {
        val hash = GeoFireUtils.getGeoHashForLocation(
            GeoLocation(location.latitude, location.longitude)
        )
        _uiState.update { state ->
            state.copy(
                latitude = location.latitude,
                longitude = location.longitude,
                geohash = hash
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

    fun onImageSelected(uri: String?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    val isSubmitEnabled: Boolean
        get() = _uiState.value.noteContent.isNotBlank() && !_uiState.value.isSubmitting

    @SuppressLint("MissingPermission")
    fun submitNote(context: Context) {
        val currentState = _uiState.value
        if (currentState.noteContent.isBlank() || currentState.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        performSubmit(context, currentState.latitude, currentState.longitude)

    }


    fun performSubmit(context: Context, lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                // 좌표 기반 "구 동" 텍스트 추출
                val addressDisplay = getAddress(context, lat, lng)

                // Geohash 생성
                val hash = LocationUtil.getGeohash(lat, lng)

                val currentTime = System.currentTimeMillis()
                val expiresTime = currentTime + (currentState.storageHours * 3600000L)

                val request = NoteRequest(
                    authorName = "사용자",
                    content = currentState.noteContent,
                    category = currentState.selectedCategory,
                    storageHours = currentState.storageHours,
                    imageUri = currentState.selectedImageUri,
                    latitude = currentState.latitude,
                    longitude = currentState.longitude,
                    createdAt = currentTime,
                    expiresAt = expiresTime,
                    geohash = hash,
                    location = addressDisplay
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