package com.example.joopjoop.feature.note.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.note.ui.list.NoteItem
import com.example.joopjoop.feature.note.ui.list.NoteListUiState
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteListViewModel(
    private val repository: NoteRepository,
    private val fusedLocationClient: FusedLocationProviderClient
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
    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        // 진입할 때, 마지막 위치 가져옴
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                _uiState.update { state ->
                    state.copy(
                        myLatitude = it.latitude,
                        myLongitude = it.longitude
                    )
                }
            }
            // 일단 리스트를 새로고침
            loadNotes()
        }
    }
    fun loadNotes() {
        val myLat = _uiState.value.myLatitude
        val myLng = _uiState.value.myLongitude
        val currentTime = System.currentTimeMillis()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val allNotes = repository.getNotes()
            val notes = allNotes
                .filter { note ->
                    note.expiresAt.time > currentTime
                }
                .map { note ->
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        myLat,
                        myLng,
                        note.location.latitude,
                        note.location.longitude,
                        results
                    )
                    note to results[0]
                }
                .sortedBy { it.second } // float 기준 정렬
                .map { (note, distanceInMeters) ->
                    // 정렬된 순서대로 NoteItem으로 변환(거리순)
                    NoteItem(
                        id = note.noteId,
                        content = note.contentText,
                        distance = if (distanceInMeters >= 1000) "${(distanceInMeters / 1000).toInt()}km"
                        else "${distanceInMeters.toInt()}m",
                        isWithinRange = distanceInMeters <= 30f,
                        latitude = note.location.latitude,
                        longitude = note.location.longitude,
                    )
                }
            _uiState.update { it.copy(notes = notes, isLoading = false) }
        }
    }
}