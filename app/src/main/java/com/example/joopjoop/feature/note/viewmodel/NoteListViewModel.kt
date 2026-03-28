package com.example.joopjoop.feature.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.common.util.LocationUtil
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.data.location.LocationProvider
import com.example.joopjoop.feature.note.ui.list.NoteItem
import com.example.joopjoop.feature.note.ui.list.NoteListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteListViewModel(
    private val repository: NoteRepository,
//    private val fusedLocationClient: FusedLocationProviderClient
    // [변경] 직접적인 Client 대신 공통 제공자인 LocationProvider를 주입받아 사용
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteListUiState())
    val uiState: StateFlow<NoteListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            uiState
                .map { it.myLatitude to it.myLongitude }
                .distinctUntilChanged() // 값이 실제로 변했을 때만
                .collect { (lat, lng) ->
                    loadNotes()
                }
        }
        fetchCurrentLocation()
    }

//    @SuppressLint("MissingPermission")
//    private fun fetchCurrentLocation() {
//        // 진입할 때, 마지막 위치 가져옴
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            location?.let {
//                _uiState.update { state ->
//                    state.copy(
//                        myLatitude = it.latitude,
//                        myLongitude = it.longitude
//                    )
//                }
//            }
//            // 일단 리스트를 새로고침
//            loadNotes()
//        }
//    }

    /**
     * [이전 작업자 참고]
     * 기존의 addOnSuccessListener(콜백) 방식 대신,
     * 프로젝트 공통 유틸인 'LocationProvider'의 코루틴 함수를 사용
     * 이를 통해 비동기 코드를 동기 코드처럼 가독성 있게 작성할 수 있음
     */
    private fun fetchCurrentLocation() {
        viewModelScope.launch {
            // LocationProvider.kt의 getCurrentLocation() 사용
            val location = locationProvider.getCurrentLocation()
            location?.let {
                _uiState.update { state ->
                    state.copy(myLatitude = it.latitude, myLongitude = it.longitude)
                }
            }
            loadNotes()
        }
    }

    //    // 현재 loadNotes 함수는
//    // 1. DB(notes 컬렉션)에 있는 모든 문서를 일단 다 가져옴
//    // 2. filter (시간 필터링) : 모든 쪽지에서 만료 시간 필터링
//    // 3. map & distanceBetween (거리 계산): 내 위치와 모든 노트를 일일이 대조
//    // 4. sortedBy (정렬): 계산된 거리를 기준으로 정렬
//    fun loadNotes() {
//        val myLat = _uiState.value.myLatitude
//        val myLng = _uiState.value.myLongitude
//        val currentTime = System.currentTimeMillis()
//
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//
//            val allNotes = repository.getNotes()
//            val notes = allNotes
//                .filter { note ->
//                    note.expiresAt.time > currentTime
//                }
//                .map { note ->
//                    val results = FloatArray(1)
//                    Location.distanceBetween(
//                        myLat,
//                        myLng,
//                        note.location.latitude,
//                        note.location.longitude,
//                        results
//                    )
//                    note to results[0]
//                }
//                .sortedBy { it.second } // float 기준 정렬
//                .map { (note, distanceInMeters) ->
//                    // 정렬된 순서대로 NoteItem으로 변환(거리순)
//                    NoteItem(
//                        id = note.noteId,
//                        content = note.contentText,
//                        distance = if (distanceInMeters >= 1000) "${(distanceInMeters / 1000).toInt()}km"
//                        else "${distanceInMeters.toInt()}m",
//                        isWithinRange = distanceInMeters <= 30f,
//                        latitude = note.location.latitude,
//                        longitude = note.location.longitude,
//                    )
//                }
//            _uiState.update { it.copy(notes = notes, isLoading = false) }
//        }
//    }
    fun loadNotes() {
        val myLat = _uiState.value.myLatitude
        val myLng = _uiState.value.myLongitude
        val currentTime = System.currentTimeMillis()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // [수정 포인트] getNotes() 대신 위치 기반 쿼리 사용!
            // 이제 DB에서 내 주변(약 5km) 데이터만 쏙 골라옴
            val nearbyNotes = repository.getNotesByLocation(myLat, myLng)

            val notes = nearbyNotes
                .filter { note ->
                    // 유효기간이 남은 쪽지만 필터링
                    note.expiresAt.time > currentTime
                }
                .map { note ->
//                    val results = FloatArray(1)
//                    Location.distanceBetween(
//                        myLat, myLng,
//                        note.location.latitude, note.location.longitude,
//                        results
//                    )
//                    note to results[0]

                    /**
                     * [이전 작업자 참고]
                     * 안드로이드 프레임워크의 Location.distanceBetween을 직접 호출하는 대신,
                     * 프로젝트 공통 유틸인 'LocationUtil.calculateDistance'를 사용
                     * * 장점:
                     * 1. FloatArray(1)과 같은 임시 객체 생성을 내부에서 처리하여 코드가 간결해짐
                     * 2. 거리 계산 로직이 변경되어도 한 곳에서 처리 용이
                     */
                    val distanceInMeters = LocationUtil.calculateDistance(
                        startLat = myLat,
                        startLng = myLng,
                        endLat = note.location.latitude,
                        endLng = note.location.longitude
                    )

                    // 쪽지 객체와 계산된 거리(Float)를 Pair로 묶어서 반환 (이후 sortedBy에서 사용)
                    note to distanceInMeters
                }
                .sortedBy { it.second } // 거리순 정렬
                .map { (note, distanceInMeters) ->
                    // VO(Note)를 UI 전용 모델(NoteItem)로 변환
                    NoteItem(
                        id = note.noteId,
                        content = note.contentText,
                        // 거리 포맷팅 (1km 이상은 km로 표시)
                        distance = if (distanceInMeters >= 1000) "${(distanceInMeters / 1000).toInt()}km"
                        else "${distanceInMeters.toInt()}m",
                        // 30m 이내일 때만 줍기(열람) 활성화
                        isWithinRange = distanceInMeters <= 30f,
                        latitude = note.location.latitude,
                        longitude = note.location.longitude,
                    )
                }
            _uiState.update { it.copy(notes = notes, isLoading = false) }
        }
    }
}